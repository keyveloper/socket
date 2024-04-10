package org.example.types;

import lombok.Data;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Data
public class FileEndType implements MessageType{
    private final String receiver;
    private final String fileName;

    @Override
    public byte[] toBytes() {
        int RECEIVER_LENGTH_SIZE = Integer.BYTES;
        int FILE_NAME_LENGTH_SIZE = Integer.BYTES;
        ByteBuffer buffer = ByteBuffer.allocate(RECEIVER_LENGTH_SIZE + FILE_NAME_LENGTH_SIZE + receiver.length() + fileName.length());
        buffer.putInt(receiver.length());
        buffer.put(receiver.getBytes());
        buffer.putInt(fileName.length());

        buffer.put(fileName.getBytes());
        System.out.println("toBytes: " + Arrays.toString(buffer.array()));

        return buffer.array();
    }

    @Override
    public FileEndType fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int receiverLength = buffer.getInt();
        System.out.println("receiverLength: " + receiverLength);
        byte[] receiverBytes = new byte[receiverLength];
        buffer.get(receiverBytes);
        System.out.println("receiverByte: " + Arrays.toString(receiverBytes));

        int fileNameLength = buffer.getInt();
        System.out.println("file length: " + fileNameLength);
        byte[] fileNameBytes = new byte[fileNameLength];
        System.out.println("fileName: " + Arrays.toString(fileNameBytes));
        buffer.get(fileNameBytes);

        return new FileEndType(new String(receiverBytes), new String(fileNameBytes));
    }
}
