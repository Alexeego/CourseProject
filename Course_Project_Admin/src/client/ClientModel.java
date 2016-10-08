package client;

import com.fasterxml.jackson.core.type.TypeReference;
import connection.Connection;
import connection.Message;
import connection.MessageType;
import frames.AbstractFrame;
import frames.AddNewRayFrame;
import frames.RegistrationFrame;
import ray.Ray;
import user.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Alexey on 21.09.2016.
 */
public class ClientModel {
    private final ClientView view;

    public ClientModel(ClientView view) {
        this.view = view;
    }

    public enum ConnectionState {
        TRY_CONNECTION,
        AUTHORIZATION,
        REGISTRATION,
        CONNECTED,
        // Others in connected
        ADD_NEW_RAY,
        SYS_ADMIN_MANAGE_USERS
    }

    private ConnectionState nowConnectionState = ConnectionState.TRY_CONNECTION;

    public void connectAuthorization() {
        nowConnectionState = ConnectionState.AUTHORIZATION;
    }

    public void connectRegistration() {
        nowConnectionState = ConnectionState.REGISTRATION;
    }

    public void connectSuccess() {
        nowConnectionState = ConnectionState.CONNECTED;
    }

    public void connectError() {
        synchronized (lock) {
            if (nowConnectionState != ConnectionState.TRY_CONNECTION) {
                nowConnectionState = ConnectionState.TRY_CONNECTION;
                try {
                    if (connection != null)
                        connection.close();
                } catch (IOException e) {
                }
                connection = null;
                user = null;
                view.updateWindow(nowConnectionState);
            }
        }
    }

    private final Object lock = new Object();


    public boolean nowSysAdmin() {
        return user.getAccess() == -1;
    }

    public List<Ray> getRays() {
        return rays;
    }

    private ArrayList<Ray> rays = null;
    private Connection connection = null;
    private User user = null;

    private ExecutorService executor = Executors.newFixedThreadPool(1);

    public void connectionToServer(String ip, int port) {
        // Try connect
        Future<Connection> future = executor.submit(() -> {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getByName(ip), port), 5000);
            Connection connection = new Connection(socket);
            connection.connect();
            return connection;
        });
        // Wait
        while (!future.isDone()) {
            Thread.yield();
        }
        try {
            // Test connection
            connection = future.get();

            //view.showMessageInfo("Соединение установлено", null);
            connectAuthorization();
            view.updateWindow(nowConnectionState);

            // Background listener start
            executor.submit(() -> {
                try {
                    loop();
                } catch (Exception exception) {
                    System.out.println("Бай-бай");
                    connectError();
                }
            });

        } catch (Exception exception) {
            view.showMessageError("Не удалось установить соединение", "Неудачная попытка соединения");
            close();
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Events Methods

    public void authorization(String name, String password) {
        try {
            connection.send(new Message(MessageType.USER_AUTHORIZATION, Connection.transformToJson(new User(name, password))));
        } catch (Exception exception) {
            connectError();
        }
    }

    public void sendInfoMessage(String text) {
        try {
            connection.send(new Message(MessageType.DATA, text));
        } catch (IOException e) {
            connectError();
        }
    }

    public void addNewInitRay(Ray ray) {
        try {
            connection.send(new Message(MessageType.ADD_NEW_RAY, Connection.transformToJson(ray)));
        } catch (IOException e) {
            connectError();
        }
    }

    public void signOut() {
        try {
            connection.send(new Message(MessageType.USER_SIGN_OUT));
            user = null;
            connectAuthorization();
            view.updateWindow(nowConnectionState);
        } catch (IOException e) {
            connectError();
        }
    }

    // Manage windows

    public void openWindowForManageAccounts() {
        try {
            view.updateWindow(ConnectionState.SYS_ADMIN_MANAGE_USERS);
            connection.send(new Message(MessageType.GET_LIST_USERS));
        } catch (IOException e) {
            connectError();
        }
    }

    public void openWindowForRegistration() {
        connectRegistration();
        view.updateWindow(nowConnectionState);
    }

    public void openWindowForAddNewRay() {
        view.updateWindow(ConnectionState.ADD_NEW_RAY);
    }


    public void toBackPressed(AbstractFrame abstractFrame) {
        if (abstractFrame instanceof RegistrationFrame)
            connectAuthorization();
        view.updateWindow(nowConnectionState);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Loop and other methods

    public void loop() throws IOException, ClassNotFoundException {
        while (!Thread.currentThread().isInterrupted() && connection != null) {
            Message message = connection.receive();
            if (nowConnectionState == ConnectionState.AUTHORIZATION) {
                communicationAuthorization(message);
            } else if (nowConnectionState == ConnectionState.REGISTRATION) {

            } else if (nowConnectionState == ConnectionState.CONNECTED) {
                communicationConnect(message);
            }
        }
    }

    ////////////////////////////////////// Authorization
    public void communicationAuthorization(Message message) throws IOException {
        switch (message.getMessageType()) {
            case USER_ACCEPTED: {
                user = Connection.transformFromJson(new TypeReference<User>() {
                }, message.getData());
                connectSuccess();
                view.updateWindow(nowConnectionState);
                break;
            }
            case USER_NOT_FOUNDED: {
                view.showMessageError("Увы, логин или пароль неверен", "Авторизация");
                break;
            }
            case USER_ALREADY_WORK: {
                view.showMessageError("Увы, данный аккаунт сейчас используется", "Авторизация");
                break;
            }
        }
    }

    ////////////////////////////////////////////////// Connected
    public void communicationConnect(Message message) {
        switch (message.getMessageType()) {
            case DATA: {
                view.showMessageInfo(message.getData(), MessageType.DATA);
                break;
            }
            case RAY_LIST: {
                initListRays(message.getData());
                break;
            }
            case NEW_RAY_ADDED: {
                view.showMessageInfo("Рейс успешно добавлен.", null, "Добавление рейса");
                view.updateWindow(nowConnectionState);
                break;
            }
            case LIST_USERS: {
                initListUsers(message.getData());
                break;
            }
        }
    }

    private void initListUsers(String json) {
        try {
            view.showMessageInfo(Connection.transformFromJson(new TypeReference<ArrayList<User>>() {}, json), MessageType.LIST_USERS);
        } catch (IOException ignored) {
            System.out.println("exception" + ignored);
        }
    }

    private void initListRays(String json) {
        try {
            rays = Connection.transformFromJson(new TypeReference<ArrayList<Ray>>() {
            }, json);
            view.showMessageInfo(null, MessageType.RAY_LIST);
        } catch (IOException ignored) {}
    }


    ///////////////////////////////////////////////////////// Close
    protected void close() {
        try {
            if (connection != null)
                connection.close();
        } catch (IOException ignored) {
            System.out.println("Finish " + ignored);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }
}
