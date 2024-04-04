package org.example;

import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class HeaderAdder {
    public static byte[] add(MessageTypeCode messageTypeCode, byte[] bodyPacket) {
        int bodyLengthSize = Integer.BYTES;
        int typeIntSize = Integer.BYTES;
        ByteBuffer byteBuffer = ByteBuffer.allocate(bodyLengthSize + typeIntSize + bodyPacket.length);
        byteBuffer.putInt(bodyPacket.length);
        byteBuffer.putInt(messageTypeCode.ordinal());
        byteBuffer.put(bodyPacket);

        return byteBuffer.array();
    }
}
