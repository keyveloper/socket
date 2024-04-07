package org.example;

import lombok.Data;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Data
public class HeaderAdder {
    public static byte[] add(MessageTypeCode messageTypeCode, byte[] bodyPacket) {
        int bodyLengthSize = Integer.BYTES;
        int typeIntSize = Integer.BYTES;
        System.out.println("headerAdder start: " + Arrays.toString(bodyPacket) + bodyPacket.length);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bodyLengthSize + typeIntSize + bodyPacket.length);
        byteBuffer.putInt(bodyPacket.length);
        byteBuffer.putInt(messageTypeCode.ordinal());
        byteBuffer.put(bodyPacket);

        System.out.println("in HeaderAdder\n bodyLength: " + bodyPacket.length +"\n messageTypeCode: " + messageTypeCode);
        return byteBuffer.array();
    }
}
