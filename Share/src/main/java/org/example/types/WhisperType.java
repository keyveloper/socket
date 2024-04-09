package org.example.types;

import lombok.Data;
;
import java.nio.ByteBuffer;

@Data
public class WhisperType implements MessageType {
    // id = receiver or sender
    private final String id;
    private final String comment;
    @Override
    public byte[] toBytes() {
        int idSize = Integer.BYTES;
        ByteBuffer byteBuffer = ByteBuffer.allocate(idSize + id.length() + comment.length());
        byteBuffer.putInt(id.length());
        byteBuffer.put(id.getBytes());
        byteBuffer.put(comment.getBytes());

        return byteBuffer.array();
    }

    @Override
    public MessageType fromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        int idLength = byteBuffer.getInt();
        byte[] idBytes = new byte[idLength];
        byteBuffer.get(idBytes);
        byte[] commentBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(commentBytes);
        // readId, readComment logic...
        return new WhisperType(new String(idBytes), new String(commentBytes));
    }
}
