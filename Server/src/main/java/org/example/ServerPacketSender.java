package org.example;

import lombok.AllArgsConstructor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

@AllArgsConstructor
public class ServerPacketSender implements PacketSender{
    public final Socket server;
    @Override
    public void sendPacket(byte[] packet) {
        try {
            System.out.println("[[IN Server Packet Sender ]]" + Arrays.toString(packet));
            DataOutputStream dataOutputStream = new DataOutputStream(server.getOutputStream());
            dataOutputStream.write(packet, 0, packet.length);
            dataOutputStream.flush();
            System.out.println("[[IN Server Packet Sender ]] packet sent");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
