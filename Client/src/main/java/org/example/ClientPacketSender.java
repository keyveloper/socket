package org.example;

import lombok.Cleanup;
import lombok.Data;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Data
public class  ClientPacketSender implements PacketSender {
    private final Socket client;

    @Override
    public void sendPacket(byte[] packet)  {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
            dataOutputStream.write(packet, 0, packet.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
