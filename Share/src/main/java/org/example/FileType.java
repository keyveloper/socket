package org.example;

import lombok.Data;

import java.io.Serializable;
import java.nio.ByteBuffer;

@Data
public class FileType implements MessageType{
    private final Boolean isEnd;
    private final String receiver;
    private final String fileName;
    private final int seq;
    private final byte[] fileByte;
    @Override
    public byte[] toBytes() {
        int BOOLEAN_SIZE = 1;
        int RECEIVER_SIZE = Integer.BYTES;
        int FILE_NAME_SIZE = Integer.BYTES;
        int SEQ_SIZE = Integer.BYTES;
        ByteBuffer byteBuffer = ByteBuffer.allocate(BOOLEAN_SIZE + RECEIVER_SIZE + receiver.length() + FILE_NAME_SIZE + fileName.length() + SEQ_SIZE + fileByte.length);
        byteBuffer.put((byte) (isEnd ? 1 : 0));

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

        byte isEndByte = byteBuffer.get();
        boolean isEnd = isEndByte != 0;

        int receiverLength = byteBuffer.getInt();
        byte[] receiverBytes = new byte[receiverLength];
        byteBuffer.get(receiverBytes);

        int fileNameLength = byteBuffer.getInt();
        byte[] fileNameBytes = new byte[fileNameLength];
        byteBuffer.get(fileNameBytes);

        int seq = byteBuffer.getInt();

        byte[] fileBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(fileBytes);

        return new FileType(isEnd, new String(receiverBytes), new String(fileNameBytes), seq, fileBytes);
    }
}
