package org.example;

import lombok.AllArgsConstructor;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
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
        } catch (EOFException e) {
            System.out.println("End of Stream");
        } catch (SocketException e) {
            System.out.println("In sender\n Client connection was reset");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
