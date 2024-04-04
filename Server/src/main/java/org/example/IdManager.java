package org.example;

import lombok.NoArgsConstructor;

import java.net.*;
import java.util.*;

@NoArgsConstructor
public class IdManager {
    private final Object socketIdLock = new Object();
    private final HashMap<String, Socket> idSocketMap = new HashMap<>();
    private final HashMap<Socket, String> socketIdMap = new HashMap<>();


    // 1. id check
    // 2. id add
    public RegisterIdStatusType register(String id, Socket socket){
        if (id.contains("\"")) {
            RegisterIdStatusType registerIdStatusType = new RegisterIdStatusType(false);
            registerIdStatusType.setNotice("contain not allowed character \"");
            return registerIdStatusType;
        }
        if (!isDuplicationID(id)){
            synchronized ( socketIdLock ){
                idSocketMap.put(id, socket);
                socketIdMap.put(socket, id);
                System.out.println("register Success!! : " + id);
                return new RegisterIdStatusType(true);
            }
        } else {
            System.out.println("register Failed: ");
            RegisterIdStatusType registerIdStatusType = new RegisterIdStatusType(false);
            registerIdStatusType.setNotice("Already Exist");
            return registerIdStatusType;
        }
    }

    public boolean changeId(String id, Socket socket){
        if(!isDuplicationID(id)){
            synchronized ( socketIdLock ){
                idSocketMap.remove(id);
                idSocketMap.put(id, socket);
                socketIdMap.put(socket, id);
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean isDuplicationID(String id){
        synchronized ( socketIdLock ){
            return idSocketMap.containsKey(id);
        }
    }

    public String getIdBySocket(Socket socket){
        synchronized ( socketIdLock ){
            return socketIdMap.get(socket);
        }
    }

    public Socket getSocketById(String id){
        synchronized ( socketIdLock ){
            return idSocketMap.get(id);
        }
    }

    public void remove(Socket socket){
        synchronized (socketIdLock){
            String id = getIdBySocket(socket);
            idSocketMap.remove(id);
            socketIdMap.remove(socket);
        }
    }
}