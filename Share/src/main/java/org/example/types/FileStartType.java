package org.example.types;

import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class FileStartType implements MessageType {
    private final String id;
    private final String fileName;


    // only for Sender Client
    private String filePath;

    @Override
    public byte[] toBytes() {
        int RECEIVER_SIZE = 4;
        int FILE_NAME_SIZE = 4;

        ByteBuffer buffer = ByteBuffer.allocate(RECEIVER_SIZE + id.length() + FILE_NAME_SIZE + fileName.length());
        buffer.putInt(id.length());
        buffer.put(id.getBytes());
        buffer.putInt(fileName.length());
        buffer.put(fileName.getBytes());

        return buffer.array();
    }

    @Override
    public FileStartType fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int receiverLength = buffer.getInt();
        byte[] receiverBytes = new byte[receiverLength];
        buffer.get(receiverBytes);

        int fileNameLength = buffer.getInt();
        byte[] fileNameBytes = new byte[fileNameLength];
        buffer.get(fileNameBytes);

        return new FileStartType(new String(receiverBytes), new String(fileNameBytes));
    }
}
