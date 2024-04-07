package org.example;

import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

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
        System.out.println("result: " + Arrays.toString(result));
        return result;
    }

}
