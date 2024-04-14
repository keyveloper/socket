package org.example;

import lombok.Data;
import org.example.types.MessageTypeCode;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

@Data
public class ClientPacketReader implements PacketReader{
    private final Socket clientSocket;
    @Override
    public Message readPacket()  {
        try {
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            int bodyLength = dataInputStream.readInt();
            System.out.println(bodyLength);
            MessageTypeCode messageTypeCode = MessageTypeCode.values()[dataInputStream.readInt()];
            System.out.println(messageTypeCode);
            byte[] body = new byte[bodyLength];
            dataInputStream.readFully(body);
            System.out.println(Arrays.toString(body));

            return new Message(messageTypeCode, body, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
