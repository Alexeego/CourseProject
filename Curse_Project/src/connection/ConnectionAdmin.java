package connection;

import com.fasterxml.jackson.core.type.TypeReference;
import ray.Ray;

import static server.Server.*;

import user.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

/**
 * Created by Alexey on 21.09.2016.
 */
public class ConnectionAdmin extends Connection {

    protected ConnectionAdmin(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        super(socket, out, in);
    }

    public void send(Message message) throws IOException {
        synchronized (out) {
            out.writeObject(message.getMessageType().toString());
            out.writeObject(message.getData());
            out.flush();
        }
    }

    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (in) {
            MessageType type = MessageType.valueOf((String) in.readObject());
            String data = (String) in.readObject();
            return new Message(type, data);
        }
    }

    private User user;

    @Override
    public void serverMainLoop(User user) throws IOException, ClassNotFoundException {
        synchronized (rays) {
            send(new Message(MessageType.RAY_LIST, transformToJson(rays)));
        }
        this.user = user;
        do {
            Message message = receive();
            switch (message.getMessageType()) {
                case DATA: {
                    if (!message.getData().trim().equals(""))
                        sendBroadcastMessage(new Message(message.getMessageType(), "Info от " + user.getName() + ": " + message.getData().trim()));
                    break;
                }
                case ADD_NEW_RAY: {
                    Ray ray = transformFromJson(new TypeReference<Ray>() {
                    }, message.getData());
                    synchronized (rays) {
                        rays.add(ray);
                        send(new Message(MessageType.NEW_RAY_ADDED));
                        sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(rays)));
                    }
                    break;
                }
                case USER_SIGN_OUT: {
                    connectionMap.remove(user);
                    System.out.println("End run " + user.getName());
                    return;
                }
                case GET_LIST_USERS: {
                    send(new Message(MessageType.LIST_USERS, transformToJson(allUsers.values())));
                    break;
                }
                case DELETE_USER: {
                    User userEditable = transformFromJson(new TypeReference<User>() {
                    }, message.getData());
                    allUsers.remove(userEditable.getName().toUpperCase());
                    if (connectionMap.containsKey(userEditable)) {
                        connectionMap.get(userEditable).close();
                    }
                    send(new Message(MessageType.LIST_USERS, transformToJson(allUsers.values())));
                    break;
                }
                case EDIT_ACCESS_USER: {
                    User userEditable = transformFromJson(new TypeReference<User>() {
                    }, message.getData());
                    Map.Entry<String, User> pairFromDB = allUsers.entrySet().stream()
                            .filter(pair -> pair.getKey().equalsIgnoreCase(userEditable.getName()))
                            .findFirst().orElse(null);
                    if (pairFromDB != null) {
                        byte oldAccess = pairFromDB.getValue().getAccess();
                        pairFromDB.getValue().setAccess(userEditable.getAccess());
                        if (oldAccess == 1 && (pairFromDB.getValue().getAccess() == 0 || pairFromDB.getValue().getAccess() == 2)
                                && connectionMap.containsKey(pairFromDB.getValue())
                                && connectionMap.get(pairFromDB.getValue()) instanceof ConnectionAdmin) {
                            connectionMap.get(pairFromDB.getValue()).close();
                        }
                    }
                    send(new Message(MessageType.LIST_USERS, transformToJson(allUsers.values())));
                    break;
                }
            }
        } while (true);
    }
}
