package org.example.types;

import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class FileType implements MessageType {
    private final String sender;
    private final String receiver;
    private final String fileName;
    private final int seq;
    private final byte[] fileByte;
    private String filePath;
    @Override
    public byte[] toBytes() {
        int SENDER_SIZE = Integer.BYTES;
        int RECEIVER_SIZE = Integer.BYTES;
        int FILE_NAME_SIZE = Integer.BYTES;
        int SEQ_SIZE = Integer.BYTES;
        ByteBuffer byteBuffer = ByteBuffer.allocate( + SENDER_SIZE + sender.length() + RECEIVER_SIZE + receiver.length() + FILE_NAME_SIZE + fileName.length() + SEQ_SIZE + fileByte.length);

        byteBuffer.putInt(sender.length());
        byteBuffer.put(sender.getBytes());

        byteBuffer.putInt(receiver.length());
        byteBuffer.put(receiver.getBytes());

        byteBuffer.putInt(fileName.length());
        byteBuffer.put(fileName.getBytes());

        byteBuffer.putInt(seq);

        byteBuffer.put(fileByte);

        return byteBuffer.array();
    }

    @Override
    public MessageType fromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        int senderLength = byteBuffer.getInt();
        byte[] senderBytes = new byte[senderLength];
        byteBuffer.get(senderBytes);

        int receiverLength = byteBuffer.getInt();
        byte[] receiverBytes = new byte[receiverLength];
        byteBuffer.get(receiverBytes);

        int fileNameLength = byteBuffer.getInt();
        byte[] fileNameBytes = new byte[fileNameLength];
        byteBuffer.get(fileNameBytes);

        int seq = byteBuffer.getInt();

        byte[] fileBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(fileBytes);

        return new FileType(new String(senderBytes), new String(receiverBytes), new String(fileNameBytes), seq, fileBytes);
    }
}
