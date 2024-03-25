package org.example;

import java.net.Socket;

public class Message {
    private final MessageType messageType;
    private final byte[] body;

    public Message(MessageType messageType, byte[] body){
        this.messageType = messageType;
        this.body = body;

    }

    public MessageType getMessageType(){
        return messageType;
    }

    public byte[] getBody(){
        return body;
    }

}
