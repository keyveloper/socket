package org.example;

import lombok.AllArgsConstructor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@AllArgsConstructor
public class ServerPacketSender implements PacketSender{
    public final Socket server;
    @Override
    public void sendPacket(byte[] packet) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(server.getOutputStream());
            dataOutputStream.writeInt(packet.length);
            dataOutputStream.write(packet, 0, packet.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
