package org.example;

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

    private final ClientServiceGiver clientServiceGiver = new ClientServiceGiver(this, fileManagerHashMap);
    private final Socket socket;

    private ClientPacketSender clientPacketSender;
    private Boolean isRegister = false;
    private String clientId;

    public Client(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", tcpClientPort));
            System.out.print("\n[ Success connecting ] \n");
            clientPacketSender = new ClientPacketSender(socket);
            while (true) {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                int bodyLength = dataInputStream.readInt();
                if(bodyLength > 0) {
                    MessageTypeCode messageTypeCode = MessageTypeCode.values()[dataInputStream.readInt()];
                    byte[] body = new byte[bodyLength];
                    dataInputStream.readFully(body);
                    Message inMessage = new Message(messageTypeCode, body, null);
                    clientServiceGiver.service(inMessage, MessageProcessor.makeMessageType(inMessage));
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processCommand(String command) throws IOException {
        ProcessedObject processedObject = commandProcessor.extract(command, isRegister);
        // array:ost = [MessageTypeCode, messageType]
        if (processedObject.getMessageTypeCode() == MessageTypeCode.FILE_START) {
            FileSender fileSender = new FileSender((FileStartType) processedObject.getMessageType(), clientPacketSender);
            fileSender.sendFile();
        } else {
            sendPacket(processedObject.getMessageTypeCode(), processedObject.getMessageType());
        }
    }

    private void sendPacket(MessageTypeCode messageTypeCode, MessageType messageType){
        byte[] packet = PacketMaker.makePacket(messageTypeCode, messageType);
        clientPacketSender.sendPacket(packet);
    }

}