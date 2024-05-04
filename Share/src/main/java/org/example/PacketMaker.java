package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.example.types.MessageType;
import org.example.types.MessageTypeCode;
import java.nio.ByteBuffer;
import com.fasterxml.jackson.databind.ObjectMapper;

@Data
public class PacketMaker {
    public static byte[] makePacket(MessageTypeCode messageTypeCode, MessageType messageType) {
        return addHeader(messageTypeCode, messageType.toBytes());
        // body = byte[] => json -> byte
    }
    private static byte[] addHeader(MessageTypeCode messageTypeCode, byte[] body) {
        int BODY_LENGTH_SIZE = Integer.BYTES;
        int TYPE_INT = Integer.BYTES;
        ByteBuffer buffer = ByteBuffer.allocate(BODY_LENGTH_SIZE + TYPE_INT + body.length);
        buffer.putInt(body.length);
        buffer.putInt(messageTypeCode.ordinal());
        buffer.put(body);

        return buffer.array();
    }

}
