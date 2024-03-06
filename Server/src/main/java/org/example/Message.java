package org.example;

import java.net.Socket;

public class Message {
    private final MessageType messageType;
    private final String body;
    private final Socket clientSocket;

    public Message(MessageType messageType, String body, Socket clientSocket){
        this.messageType = messageType;
        this.body = body;
        this.clientSocket = clientSocket;
    }

    public MessageType getMessageType(){
        return messageType;
    }

    public String getBody(){
        return body;
    }

    public Socket getClientSocket(){
        return clientSocket;
    }
}
