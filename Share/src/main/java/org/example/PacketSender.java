package org.example;

import java.io.IOException;
import java.net.Socket;

public interface PacketSender{
    public void sendPacket(byte[] packet) throws IOException;

}
