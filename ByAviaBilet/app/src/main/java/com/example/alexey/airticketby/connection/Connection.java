package com.example.alexey.airticketby.connection;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.Socket;

/**
 * Created by Alexey on 07.09.2016.
 */
public class Connection implements Closeable {

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Connection(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        this.socket = socket;
        this.out = out;
        this.in = in;
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

    @Override
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
