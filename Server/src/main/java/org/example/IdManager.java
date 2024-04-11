package org.example;

import lombok.NoArgsConstructor;
import org.example.types.RegisterIdStatusType;

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
        RegisterIdStatusType statusType;
        if (id.contains("\"")) {
            statusType = new RegisterIdStatusType(false, id, "contain not allowed character \"");
        } else {
            synchronized (socketIdLock) {
                if (!idSocketMap.containsKey(id)) {
                    idSocketMap.put(id, socket);
                    socketIdMap.put(socket, id);

                    statusType = new RegisterIdStatusType(true, id,"register Success!!");
                } else {
                    statusType = new RegisterIdStatusType(false, id,"Already Exsit");
                }
            }
        }

        return statusType;
    }

    public RegisterIdStatusType changeId(String newId, Socket socket){
        RegisterIdStatusType statusType;
        synchronized (socketIdLock) {
            if (socketIdMap.containsKey(socket)) {
                if (!idSocketMap.containsKey(newId)) {
                    String oldId = getIdBySocket(socket);
                    idSocketMap.remove(oldId);
                    idSocketMap.put(newId, socket);
                    socketIdMap.put(socket, newId);
                    statusType = new RegisterIdStatusType(true, newId,"change Success");
                } else {
                    statusType = new RegisterIdStatusType(false, newId, "Already Exist");
                }
            } else {
                statusType = new RegisterIdStatusType(false, newId, "Register first");
            }
        }
        return statusType;
    }

    public void remove(Socket socket) {
        synchronized (socketIdLock) {
            String target = socketIdMap.get(socket);
            idSocketMap.remove(target);
            socketIdMap.remove(socket);
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

}