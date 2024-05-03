package org.example;

import lombok.NoArgsConstructor;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

@NoArgsConstructor
public class HandlerManger {
    private final HashMap<Socket, ClientHandler> handlerMap = new HashMap<>();
    private final Object handlerLock = new Object();

    public void register(Socket clientSocket, ClientHandler clientHandler) {
        synchronized (handlerLock) {
            handlerMap.put(clientSocket, clientHandler);
        }
    }

    public ClientHandler get(Socket clientSocket) {
        ClientHandler clientHandler;
        synchronized (handlerLock) {
            clientHandler = handlerMap.get(clientSocket);
        }
        return clientHandler;
    }

    public void remove(Socket clientSocket) {
        synchronized (handlerLock) {
            handlerMap.remove(clientSocket);
        }
    }

    public ArrayList<ClientHandler> getAllHandler() {
        return new ArrayList<>(handlerMap.values());
    }

    public boolean checkEmpty() {
        return handlerMap.isEmpty();
    }
}
