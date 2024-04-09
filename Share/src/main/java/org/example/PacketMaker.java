package org.example;

import lombok.Data;
import org.example.types.MessageType;
import org.example.types.MessageTypeCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Data
public class PacketMaker {
    public static byte[] makePacket(MessageTypeCode messageTypeCode, MessageType messageType) throws IOException {
        // why null

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(messageType);
        objectOutputStream.flush();
        byte[] body = byteArrayOutputStream.toByteArray();
        byte[] result = HeaderAdder.add(messageTypeCode, body);
        return result;
    }

}
