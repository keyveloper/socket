package org.example;

import lombok.Data;
import org.example.types.MessageType;
import org.example.types.MessageTypeCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

@Data
public class PacketMaker {
    public static byte[] makePacket(MessageTypeCode messageTypeCode, MessageType messageType) {
        // why null
        System.out.println("make Packet Start!");
        byte[] typeBytes = messageType.toBytes();
        System.out.println("typeBytes: " + Arrays.toString(typeBytes));
        return addHeader(messageTypeCode, typeBytes);
    }
    private static byte[] addHeader(MessageTypeCode messageTypeCode, byte[] packet) {
        int BODY_LENGTH_SIZE = Integer.BYTES;
        int TYPE_INT = Integer.BYTES;
        ByteBuffer buffer = ByteBuffer.allocate(BODY_LENGTH_SIZE + TYPE_INT + packet.length);
        buffer.putInt(packet.length);
        buffer.putInt(messageTypeCode.ordinal());
        buffer.put(packet);

        System.out.println("added header! : "+ Arrays.toString(buffer.array()));
        return buffer.array();
    }

}
