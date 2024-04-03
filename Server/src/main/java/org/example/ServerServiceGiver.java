package org.example;

import lombok.Data;

import java.net.Socket;
import java.util.HashMap;

@Data
public class ServerServiceGiver implements ServiceGiver{
    private final IdManager idManager;
    private final CountManager countManager;
    private final ClientHandler clientHandler;

    @Override
    public void service(Message message) {
        InputMessageProcessor inputMessageProcessor = new InputMessageProcessor();
        switch (message.getMessageTypeCode()) {
            case REGISTER_ID:
                MessageType registerId = inputMessageProcessor.makeType(message);
                registerId((RegisterIdType) registerId, message.getClientSocket());
        }
    }

    public void registerId(RegisterIdType registerIdType, Socket socket) {
        String id = registerIdType.getId();
        if (idManager.register(id, socket)){
            System.out.println("Id Reigsterd complete");
            countManager.register(socket);
            synchronized ( handlerLock ){
                handlerMap.get(socket).sendTypeOnly(MessageTypeLibrary.REGISTER_SUCCESS);
                System.out.println("send ResisterSuceess");
            }
        } else {
            synchronized ( handlerLock ){
                handlerMap.get(socket).sendTypeOnly(MessageTypeLibrary.ALREADY_EXIST_ID);
                System.out.println("Already Exist ID");
            }
        }
    }
}

