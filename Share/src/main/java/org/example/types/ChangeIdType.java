package org.example.types;

import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class ChangeIdType implements MessageType {
    private final String oldId;
    private final String newId;

    @Override
    public byte[] toBytes() {
        int OLD_ID_SIZE = Integer.BYTES;
        ByteBuffer buffer = ByteBuffer.allocate(OLD_ID_SIZE + oldId.length() + newId.length());
        buffer.putInt(oldId.length());
        buffer.put(oldId.getBytes());
        buffer.put(newId.getBytes());

        return buffer.array();
    }

    public MessageType fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int oldIdLength = buffer.getInt();
        byte[] oldIdBytes = new byte[oldIdLength];
        buffer.get(oldIdBytes);
        byte[] newIdBytes = new byte[buffer.remaining()];
        buffer.get(newIdBytes);

        return new ChangeIdType(new String(oldIdBytes), new String(newIdBytes));
    }
}