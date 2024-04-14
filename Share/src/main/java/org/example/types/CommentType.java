package org.example.types;

import lombok.Data;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Data
public class CommentType implements MessageType {
    private final String senderId;
    private final String comment;
    @Override
    public byte[] toBytes() {
        int idSize = Integer.BYTES;
        ByteBuffer byteBuffer = ByteBuffer.allocate(idSize + senderId.length() + comment.length());
        byteBuffer.putInt(senderId.length());
        byteBuffer.put(senderId.getBytes());
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

        return new CommentType(new String(idBytes), new String(commentBytes));
    }
}
