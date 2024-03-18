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

    public void register(Socket socket){
        synchronized ( countLock ) {
            messageCountMap.put(socket, 0);
        }
    }
    public void add(Socket socket){
        synchronized ( countLock ) {
            if (messageCountMap.containsKey(socket)){
                messageCountMap.put(socket, messageCountMap.get(socket) + 1);
            }
        }
    }

    public void remove(Socket socket){
        synchronized ( countLock ){
            messageCountMap.remove(socket);
        }
    }
    public Integer get(Socket socket){
        return messageCountMap.get(socket);
    }

}
