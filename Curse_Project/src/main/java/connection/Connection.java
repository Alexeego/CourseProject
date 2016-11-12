package connection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.GenericDAOException;
import user.User;

import java.io.*;
import java.net.Socket;

public abstract class Connection implements Closeable {

    protected final Socket socket;
    protected final ObjectOutputStream out;
    protected final ObjectInputStream in;

    protected Connection(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        this.socket = socket;
        this.out = out;
        this.in = in;
    }

    public static Connection build(Socket socket) throws IOException, ClassNotFoundException {
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        outputStream.writeObject(MessageType.CONNECT_REQUEST.toString());
        outputStream.flush();
        switch (MessageType.valueOf((String)inputStream.readObject())){
            case USER_ANDROID:
                return new ConnectionAndroid(socket, outputStream, inputStream);
            case USER_ADMIN:
                return new ConnectionAdmin(socket, outputStream, inputStream);
        }
        outputStream.close();
        inputStream.close();
        socket.close();
        throw new IOException();
    }

    public abstract void send(Message message) throws IOException;

    public abstract Message receive() throws IOException, ClassNotFoundException;

    public abstract void serverMainLoop(User user) throws IOException, ClassNotFoundException, GenericDAOException;

    public static String transformToJson(Object object) throws IOException {
        StringWriter writer = new StringWriter();
        new ObjectMapper().writeValue(writer, object);
        return writer.toString();
    }

    public static <A> A transformFromJson(final TypeReference<A> type, final String json) throws IOException {
        return new ObjectMapper().readValue(json, type);
    }


    @Override
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }
}
