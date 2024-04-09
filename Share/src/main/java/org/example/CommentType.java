package org.example;

import lombok.Data;

import java.io.Serializable;
import java.nio.ByteBuffer;

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
        byte[] idBytes = new byte[byteBuffer.getInt()];
        byteBuffer.put(idBytes);
        byte[] commentBytes = new byte[byteBuffer.remaining()];
        byteBuffer.put(commentBytes);

        return new CommentType(new String(idBytes), new String(commentBytes));
    }
}
