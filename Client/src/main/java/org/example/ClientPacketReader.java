package org.example;

import lombok.Data;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;

@Data
public class ClientPacketReader implements PacketReader{
    private final Socket client;

    @Override
    public Message readPacket() throws IOException {
        DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
        int bodyLength = dataInputStream.readInt();
        MessageType messageType = MessageType.values()[dataInputStream.readInt()];
        byte[] body = new byte[bodyLength];
        dataInputStream.readFully(body);

        return new Message(bodyLength, messageType, body);
    }

}
