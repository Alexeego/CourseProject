package connection;

import com.fasterxml.jackson.core.type.TypeReference;
import ray.Ray;
import server.Server;
import user.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Alexey on 21.09.2016.
 */
public class ConnectionAdmin extends Connection {

    protected ConnectionAdmin(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        super(socket, out, in);
    }

    public void send(Message message) throws IOException {
        synchronized (out){
            out.writeObject(message.getMessageType().toString());
            out.writeObject(message.getData());
            out.flush();
        }
    }

    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (in){
            MessageType type = MessageType.valueOf((String)in.readObject());
            String data = (String) in.readObject();
            return new Message(type, data);
        }
    }

    private User user;

    @Override
    public void serverMainLoop(User user) throws IOException, ClassNotFoundException {
        synchronized (Server.rays) {
            send(new Message(MessageType.RAY_LIST, Connection.transformToJson(Server.rays)));
        }
        this.user = user;
        do{
            Message message = receive();
            switch (message.getMessageType()){
                case DATA: {
                    System.out.println(message.getData());
                    Server.sendBroadcastMessage(message);
                    break;
                }
                case ADD_NEW_RAY: {
                    Ray ray = transformFromJson(new TypeReference<Ray>() {}, message.getData());
                    synchronized (Server.rays){
                        Server.rays.add(ray);
                        send(new Message(MessageType.NEW_RAY_ADDED));
                        Server.sendBroadcastMessage(new Message(MessageType.RAY_LIST, transformToJson(Server.rays)));
                    }
                    break;
                }
                case USER_SIGN_OUT:{
                    Server.connectionMap.remove(user);
                    System.out.println("End run " + user.name);
                    return;
                }
            }
        } while (true);
    }
}
