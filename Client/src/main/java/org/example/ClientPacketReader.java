package org.example;

import lombok.Data;
import org.example.types.MessageTypeCode;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

@Data
public class ClientPacketReader implements PacketReader{
    private final Socket clientSocket;
    @Override
    public Message readPacket()  {
        try {
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            int bodyLength = dataInputStream.readInt();
            MessageTypeCode messageTypeCode = MessageTypeCode.values()[dataInputStream.readInt()];
            byte[] body = new byte[bodyLength];
            dataInputStream.readFully(body);

            return new Message(messageTypeCode, body, null);
        } catch (SocketException e) {
            System.out.println("connection closed!!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
