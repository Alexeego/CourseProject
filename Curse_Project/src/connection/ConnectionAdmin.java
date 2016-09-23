package connection;

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

    @Override
    public void serverMainLoop(User user) throws IOException, ClassNotFoundException {
        do{
            Message message = receive();
            switch (message.getMessageType()){
                case DATA: {
                    System.out.println(message.getData());
                    send(new Message(MessageType.DATA, "Сервер принял приветствие"));
                    break;
                }
            }
        } while (true);
    }
}
