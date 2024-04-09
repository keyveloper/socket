package org.example;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

@Setter
@Getter
public class Client implements Runnable {
    private final int tcpClientPort = 9999;

    private final CommandProcessor commandProcessor = new CommandProcessor(this);

    private final ClientServiceGiver clientServiceGiver = new ClientServiceGiver(this);
    private final Socket socket;

    private ClientPacketSender clientPacketSender;

    private Boolean isRegister;

    private final FileManager fileManager = new FileManager(this);

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
                    Message inMessage = new Message(bodyLength, messageTypeCode, body, null);
                    clientServiceGiver.service(inMessage, MessageProcessor.makeMessageType(inMessage));
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processCommand(String command) throws IOException {
        ProcessedObject processedObject = commandProcessor.extract(command);
        // array:ost = [MessageTypeCode, messageType]
        sendPacket(processedObject.getMessageTypeCode(), processedObject.getMessageType());
    }

    public void fileSend(FileType fileType) throws IOException{
        byte[] filePacket = PacketMaker.makePacket(MessageTypeCode.FILE, fileType);
        clientPacketSender.sendPacket(filePacket);
    }

    private void sendPacket(MessageTypeCode messageTypeCode, MessageType messageType) throws IOException {

        byte[] packet = PacketMaker.makePacket(messageTypeCode, messageType);
        clientPacketSender.sendPacket(packet);
    }

}