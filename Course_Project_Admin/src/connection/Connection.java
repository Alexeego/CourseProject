package connection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import user.User;

import java.io.*;
import java.net.Socket;

public class Connection implements Closeable {

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
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

    public static String transformToJson(Object object) throws IOException {
        StringWriter writer = new StringWriter();
        new ObjectMapper().writeValue(writer, object);
        return writer.toString();
    }
    public static <A> A transformFromJson(final TypeReference<A> type, final String json) throws IOException {
        return new ObjectMapper().readValue(json, type);
    }


    public void connect() throws Exception {
        if (MessageType.valueOf((String) in.readObject()) == MessageType.CONNECT_REQUEST) {
            out.writeObject(MessageType.USER_ADMIN.toString());
            out.flush();

        } else throw new Exception("Не удалось подключиться к серверу");
    }


    @Override
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

}
