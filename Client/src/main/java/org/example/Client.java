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

    private Boolean isRegister;

    public Client(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
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
        System.out.println("in client SendPacket \ntotal Packet : " + Arrays.toString(packet));
        ClientPacketSender clientPacketSender = new ClientPacketSender(socket);
        clientPacketSender.sendPacket(packet);
    }
}