package client;

import com.fasterxml.jackson.core.type.TypeReference;
import connection.Connection;
import connection.Message;
import connection.MessageType;
import frames.AuthorizationFrame;
import user.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
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


    public enum ConnectionState{
        TRY_CONNECTION,
        AUTHORIZATION,
        REGISTRATION,
        CONNECTED
    }
    private ConnectionState nowConnectionState = ConnectionState.TRY_CONNECTION;

    public void connectAuthorization(){
        nowConnectionState = ConnectionState.AUTHORIZATION;
    }
    public void connectRegistration(){
        nowConnectionState = ConnectionState.REGISTRATION;
    }
    public void connectSuccess(){
        nowConnectionState = ConnectionState.CONNECTED;
    }
    public void connectError(){
        synchronized (lock) {
            if(nowConnectionState != ConnectionState.TRY_CONNECTION) {
                nowConnectionState = ConnectionState.TRY_CONNECTION;
                try {
                    if (connection != null)
                        connection.close();
                } catch (IOException e) {}
                connection = null;
                user = null;
                view.updateWindow(ConnectionState.TRY_CONNECTION);
            }
        }
    }
    private final Object lock = new Object();

    private Connection connection = null;
    private User user = null;


    private ExecutorService executor = Executors.newFixedThreadPool(1);

    public void connectionToServer(String ip, int port) {
        Future<Connection> future = executor.submit(() -> {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getByName(ip), port), 5000);
            Connection connection = new Connection(socket);
            connection.connect();
            return connection;
        });
        while (!future.isDone()) {
            Thread.yield();
        }
        try {
            connection = future.get();
            view.showMessageInfo("Соединение установлено");
            connectAuthorization();
            view.updateWindow(nowConnectionState);

            // Background listener start
            executor.submit(() -> {
                try {
                    loop();
                } catch (Exception exception){
                    System.out.println("Бай-бай");
                    connectError();
                }
            });

        } catch (Exception ignored) {
            System.out.println(ignored);
            close();
        }
    }

    public void authorization(String name, String password){
        try {
            connection.send(new Message(MessageType.USER_AUTHORIZATION, Connection.transformToJson(new User(name, password, true))));
        } catch (Exception exception){
            connectError();
        }
    }


    public void loop() throws IOException, ClassNotFoundException {
        while (!Thread.currentThread().isInterrupted() && connection != null){
            Message message = connection.receive();
            if(nowConnectionState == ConnectionState.AUTHORIZATION){
                communicationAuthorization(message);
            } else if(nowConnectionState == ConnectionState.REGISTRATION){

            } else if(nowConnectionState == ConnectionState.CONNECTED) {
                communicationConnect(message);
            }
        }
    }


    public void communicationAuthorization(Message message) throws IOException {
        switch (message.getMessageType()){
            case USER_ACCEPTED: {
                user = Connection.transformFromJson(new TypeReference<User>() {}, message.getData());
                connectSuccess();
                view.showMessageInfo("Ура. Вы авторизованы " + user.name, "Авторизация");
                break;
            }
            case USER_NOT_FOUNDED: {
                view.showMessageError("Увы, логин или пароль неверен", "Авторизация");
            }
        }
    }

    public void communicationConnect(Message message){
        switch (message.getMessageType()) {
            case DATA: {
                view.showMessageInfo(message.getData());
                break;
            }
        }
    }

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
