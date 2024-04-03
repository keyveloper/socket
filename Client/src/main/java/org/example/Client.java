package org.example;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class Client implements Runnable {
    private final int tcpClientPort = Share.portNum;
    Socket socket;

    public Client() {
        socket = new Socket();
    }
    public void run() {
        try {
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", tcpClientPort));
            System.out.print("\n[ Success connecting ] \n");
            ClientPacketReader clientPacketReader = new ClientPacketReader(socket);
            while (true) {
                Message receivedMessage = clientPacketReader.readPacket();


            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processCommand(String command) throws IOException {
        CommandSeparator commandSeparator = new CommandSeparator();
        commandSeparator.separate(command);
        byte[] packet = makePacket(commandSeparator.getMessageTypeCode(), commandSeparator.getContentMap());
        sendPacket(packet);
        // MessageType
        // contentMap -> 그냥 넘겨주기
    }

    private byte[] makePacket(MessageTypeCode messageType, HashMap<String, Object> contentMap) throws IOException {
        return PacketMaker.makePacket(messageType, contentMap);
    }

    private void sendPacket(byte[] packet) throws IOException {
        ClientPacketSender clientPacketSender = new ClientPacketSender(socket);
        clientPacketSender.sendPacket(packet);
    }
}