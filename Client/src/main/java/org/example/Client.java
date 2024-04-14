package org.example;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.types.FileStartType;
import org.example.types.MessageType;
import org.example.types.MessageTypeCode;

import java.io.*;
import java.net.*;
import java.util.HashMap;

@Setter
@Getter
public class Client implements Runnable {
    private final int tcpClientPort = 9999;

    private final CommandProcessor commandProcessor = new CommandProcessor(this);
    private final HashMap<String, FileManager> fileManagerHashMap = new HashMap<>();

    private final Socket socket;
    private final ClientPacketSender clientPacketSender;
    private final ServerHandler serverHandler;
    private final ClientServiceGiver clientServiceGiver = new ClientServiceGiver(this, fileManagerHashMap);

    private Boolean isRegister = false;
    private String clientId;

    public Client(Socket socket) {
        this.socket = socket;
        clientPacketSender = new ClientPacketSender(socket);
        serverHandler = new ServerHandler(this, clientPacketSender);
    }

    public void run() {
        try {
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", tcpClientPort));
            System.out.print("\n[ Success connecting ] \n");
            Thread thread = new Thread(serverHandler);
            thread.start();

        } catch (IOException e) {
            System.out.println("Server connection End");
        }
    }

    public void processCommand(String command) throws IOException {
        ProcessedObject processedObject = commandProcessor.extract(command, isRegister);
        // array:ost = [MessageTypeCode, messageType]
        if (processedObject.getMessageTypeCode() == MessageTypeCode.FILE_START) {
            serverHandler.sendFileStart((FileStartType) processedObject.getMessageType());
        }
        serverHandler.sendPacket(processedObject.getMessageTypeCode(), processedObject.getMessageType());
    }

    public void service(Message message) {
        clientServiceGiver.service(message, MessageProcessor.makeMessageType(message));
    }

}