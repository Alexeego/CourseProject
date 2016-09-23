package com.example.alexey.airticketby.connection;


/**
 * Created by Alexey on 07.09.2016.
 */
public class Message{
    private String data;
    private MessageType messageType;

    public Message(MessageType messageType, String data) {
        this.messageType = messageType;
        this.data = data;
    }

    public Message(MessageType messageType) {
        this.messageType = messageType;
        this.data = "";
    }

    public String getData() {
        return data;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
