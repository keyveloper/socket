package org.example;

import lombok.AllArgsConstructor;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

@AllArgsConstructor
public class ServerPacketReader implements PacketReader{
    private final Socket clientSocket;

    @Override
    public Message readPacket() {
        try {
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            int bodyLength = dataInputStream.readInt();
            MessageTypeCode messageTypeCode = MessageTypeCode.values()[dataInputStream.readInt()];
            byte[] body = new byte[bodyLength];
            dataInputStream.readFully(body);

            return new Message(bodyLength, messageTypeCode, body, clientSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
