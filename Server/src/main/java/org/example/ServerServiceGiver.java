package org.example;

import lombok.Data;

import java.net.Socket;
import java.util.HashMap;

@Data
public class ServerServiceGiver implements ServiceGiver{
    private final IdManager idManager;
    private final CountManager countManager;

    @Override
    public void service(MessageTypeCode messageTypeCode) {
        switch (messageTypeCode) {
            case REGISTER_ID:
                registerId();
        }
    }


    public void registerId() {
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

