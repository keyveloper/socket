package org.example;


import java.io.IOException;

public interface ServiceGiver {

    public void service(Message message, MessageType messageType) throws IOException;
}
