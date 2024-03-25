package org.example;

import java.nio.*;

public class HeaderMaker {
    public static byte[] makeHeader(MessageType messageType, byte[] body) {
        // bodyLength + bodytype + body
        int bodyLength = body.length;
        ByteBuffer resultBuffer = ByteBuffer.allocate(Integer.BYTES * 2 + bodyLength);
        resultBuffer.putInt(bodyLength);
        resultBuffer.putInt(messageType.ordinal());
        resultBuffer.put(body);

        return resultBuffer.array();
    }
}
