package org.example;

import lombok.NoArgsConstructor;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

@NoArgsConstructor
public class Client implements Runnable {
    private final int tcpClientPort = 9999;

    private final CommandProcessor commandProcessor = new CommandProcessor();

    private final ClientServiceGiver clientServiceGiver = new ClientServiceGiver();
    private Socket socket;

    public void run() {
        try {
            socket = new Socket();
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", tcpClientPort));
            System.out.print("\n[ Success connecting ] \n");
            ClientPacketReader clientPacketReader = new ClientPacketReader(socket);
            while (true) {
                Message receivedMessage = clientPacketReader.readPacket();
                clientServiceGiver.service(receivedMessage, MessageProcessor.makeMessageType(receivedMessage));

                if (receivedMessage.getMessageTypeCode() == MessageTypeCode.FIN_ACK) {
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processCommand(String command) throws IOException {
        ArrayList<Object> arrayList = commandProcessor.extract(command);
        // array:ost = [MessageTypeCode, messageType]
        sendPacket((MessageTypeCode) arrayList.get(0), (MessageType) arrayList.get(1));

    }

    private void sendPacket(MessageTypeCode messageTypeCode, MessageType messageType) throws IOException {
        byte[] packet = PacketMaker.makePacket(messageTypeCode, messageType);
        ClientPacketSender clientPacketSender = new ClientPacketSender(socket);
        clientPacketSender.sendPacket(packet);
    }
}