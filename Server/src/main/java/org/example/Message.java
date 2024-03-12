package org.example;

import java.net.Socket;

public class Message {
    private final MessageType messageType;
    private final byte[] body;
    private final Socket clientSocket;

    public Message(MessageType messageType, byte[] body, Socket clientSocket){
        this.messageType = messageType;
        this.body = body;
        this.clientSocket = clientSocket;
    }

    public MessageType getMessageType(){
        return messageType;
    }

    public byte[] getBody(){
        return body;
    }

    public Socket getClientSocket(){
        return clientSocket;
    }
}
