package org.example;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
public class FileTokenManger {
    private final HashMap<UUID, HashMap<String, Socket>> fileTokenMap = new HashMap<>();
    private final Object fileTokenLock = new Object();

    public void put(UUID tokenId, HashMap<String, Socket> actorMap) {
        synchronized (fileTokenLock) {
            fileTokenMap.put(tokenId, actorMap);
        }
    }


    public Socket getReceiver(UUID tokenID) {
        synchronized (fileTokenLock) {
            return fileTokenMap.get(tokenID).get("receiver");
        }
    }

    public void remove(UUID tokenId) {
        synchronized (fileTokenLock) {
            fileTokenMap.remove(tokenId);
        }
    }

}
