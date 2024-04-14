package org.example;

import lombok.AllArgsConstructor;
import org.example.types.MessageTypeCode;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

@AllArgsConstructor
public class ServerPacketReader implements PacketReader{
    private final Socket clientSocket;

    @Override
    public Message readPacket() {
        System.out.println("start readPacket in Server");
        try {
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            int bodyLength = dataInputStream.readInt();
            MessageTypeCode messageTypeCode = MessageTypeCode.values()[dataInputStream.readInt()];
            byte[] body = new byte[bodyLength];
            dataInputStream.readFully(body);
            System.out.println("In ServerPacket Read\nbodyLength: " + bodyLength +"\nMessage Type Code: " + messageTypeCode + "\n body: " + Arrays.toString(body));

            return new Message(messageTypeCode, body, clientSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
