package org.example;

import java.net.*;
import java.util.*;

public class CountManager {
    private final HashMap<Socket, Integer> messageCountMap = new HashMap<>();
    private final Object countLock = new Object();

    private final Server server;

    public CountManager(Server server){
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    public void registerSocket(Socket socket){
        synchronized ( countLock ) {
            messageCountMap.put(socket, 0);
        }
    }
    public void addCount(Socket socket){
        synchronized ( countLock ) {
            if (messageCountMap.containsKey(socket)){
                messageCountMap.put(socket, messageCountMap.get(socket));
            }
        }
    }

    public void removeCount(Socket socket){
        synchronized ( countLock ){
            messageCountMap.remove(socket);
        }
    }
    public Integer getCoount(Socket socket){
        return messageCountMap.get(socket);
    }

}
