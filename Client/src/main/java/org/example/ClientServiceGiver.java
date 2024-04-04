package org.example;

import java.io.IOException;

public class ClientServiceGiver implements ServiceGiver{
    @Override
    public void service(Message message, MessageType messageType) throws IOException {
        switch (message.getMessageTypeCode()) {
            case REGISTER_STATUS:
                System.out.println("service start: " + messageType.toString());
                break;
        }
    }
}
