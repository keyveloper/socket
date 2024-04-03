package org.example;

import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

@Data
public class HeadAdder {
    public static byte[] add(MessageTypeCode typeCode, byte[] bodyPacket) {
        int bodyLengthSize = Integer.BYTES;
        int typeIntSize = Integer.BYTES;
        ByteBuffer byteBuffer = ByteBuffer.allocate(bodyLengthSize + typeIntSize + bodyPacket.length);
        byteBuffer.putInt(bodyPacket.length);
        byteBuffer.putInt(typeCode.ordinal());
        byteBuffer.put(bodyPacket);

        return byteBuffer.array();
    }
}
