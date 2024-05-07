package org.example;

import lombok.AllArgsConstructor;
import org.example.types.MessageTypeCode;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

@AllArgsConstructor
public class ServerPacketReader implements PacketReader{
    private final Socket clientSocket;

    @Override
    public Message readPacket() {
        try {
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            int bodyLength = dataInputStream.readInt();
            System.out.println("read body Length!!" + bodyLength + "\n");
            MessageTypeCode messageTypeCode = MessageTypeCode.values()[dataInputStream.readInt()];
            System.out.println("Message Type Code: " + messageTypeCode + "\n");
            if (messageTypeCode == MessageTypeCode.FIN) {
                return null;
            }
            byte[] body = new byte[bodyLength];
            dataInputStream.readFully(body);

            return new Message(messageTypeCode, body, clientSocket);
        } catch (EOFException e) {
            System.out.println("End of Stream");
        } catch (SocketException e) {
            System.out.println("In reader\n Client connection was reset");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
