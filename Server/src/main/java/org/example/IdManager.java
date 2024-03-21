package org.example;

import java.net.*;
import java.util.*;

public class IdManager {
    private final Object socketIdLock = new Object();
    private final HashMap<String, Socket> idSocketMap = new HashMap<>();
    private final HashMap<Socket, String> socketIdMap = new HashMap<>();

    private final Server server;

    public IdManager(Server server) {
        this.server = server;
    }

    // 1. id check
    // 2. id add
    public boolean register(String id, Socket socket) {
        if (!isDuplicationID(id)) {
            synchronized (socketIdLock) {
                idSocketMap.put(id, socket);
                socketIdMap.put(socket, id);
                System.out.println("register Success!! : " + id);
                return true;
            }
        } else {
            System.out.println("register Failed: ");
            return false;
        }
    }

    public boolean changeId(String id, Socket socket) {
        if (!isDuplicationID(id)) {
            synchronized (socketIdLock) {
                idSocketMap.remove(id);
                idSocketMap.put(id, socket);
                socketIdMap.put(socket, id);
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean isDuplicationID(String id) {
        synchronized (socketIdLock) {
            return idSocketMap.containsKey(id);
        }
    }

    public boolean checkRegisterSuccess(String id) {
        synchronized (socketIdLock) {
            return idSocketMap.containsKey(id) && isDuplicationID(id);
        }
    }

    public HashMap<Socket, String> getIdSocketMap() {
        synchronized (socketIdLock) {
            return socketIdMap;
        }
    }

    public HashMap<String, Socket> getSocketIdMap() {
        synchronized (socketIdLock) {
            return idSocketMap;
        }
    }

    public Server getServer() {
        return server;
    }

    public String getIdBySocket(Socket socket) {
        synchronized (socketIdLock) {
            return socketIdMap.get(socket);
        }
    }

    public Socket getSocketById(String id) {
        synchronized (socketIdLock) {
            return idSocketMap.get(id);
        }
    }

    public void remove(Socket socket) {
        synchronized (socketIdLock) {
            String id = getIdBySocket(socket);
            idSocketMap.remove(id);
            socketIdMap.remove(socket);
        }
    }
}