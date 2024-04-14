package org.example;

import java.io.IOException;


public interface PacketSender{
    public void sendPacket(byte[] packet) throws IOException;

}
