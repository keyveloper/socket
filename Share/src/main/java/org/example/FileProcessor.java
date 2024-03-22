package org.example;

import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.example.Share.*;
import static org.example.TypeChange.intToByteArray;

public class FileProcessor {

    public static int getReceiverIdLength(byte[] body) {
        return ByteBuffer.wrap(body, 0, 4).getInt();
    }

    public static String getReceiverId(byte[] body) {
        int receiverIdLength = ByteBuffer.wrap(body, 0, 4).getInt();
        return new String(body, 4, receiverIdLength);
    }

    public static int getFileSeq(byte[] body) {
        int fileNameSize = 4;
        int seqSize = 4;
        ByteBuffer byteBuffer = ByteBuffer.allocate(seqSize);
        ByteBuffer.wrap(body, fileNameSize, seqSize);
        return byteBuffer.getInt();
    }

    public static String getFileName(byte[] body) {
        int fileNameSize = 4;
        return new String(body, 0, fileNameSize, StandardCharsets.UTF_8);
    }

    public static byte[] getFileByte(byte[] body) {
        int fileNameSize = 4;
        int seqSize = 4;
        int fileSize = body.length - fileNameSize - seqSize;
        ByteBuffer byteBuffer = ByteBuffer.allocate(fileSize);
        return ByteBuffer.wrap(body, fileNameSize + seqSize, fileSize).array();
    }

    public static byte[] getTestFileHeader(MessageType messageType, String receiver, String fileName, int seq, byte[] body) {

        // idlength(4) + id + fileName(4) + seq(4)

        String onlyName = fileName.substring(0, fileName.lastIndexOf('.'));
        byte[] fileNameByte = onlyName.getBytes(StandardCharsets.UTF_8);
        System.out.println("\n fileName Byte: " + Arrays.toString(fileNameByte));

        System.out.println("fileNameByteSize: " + fileNameByte.length);
        if (fileNameByte.length > 4) {
            throw new FileNameLengthException("FileName length  must be under 4");
        }
        int idLengthByteSize = 4;
        int idByteSize = receiver.length();
        int bodyLengthByteSize = 4;
        int typeByteSize = 4;
        int fileNameByteSize = 4;
        int seqByteSize = 4;
        int fileByteSize = body.length;

        System.out.println("fileByteSize: " + fileByteSize);

        ByteBuffer byteBuffer = ByteBuffer.allocate(idLengthByteSize + idByteSize + bodyLengthByteSize + typeByteSize + fileNameByteSize + seqByteSize + fileByteSize);
        System.out.println("byteBuffer length: " + byteBuffer.capacity());


        // body length
        byteBuffer.putInt(idLengthByteSize + idByteSize + fileNameByteSize + seqByteSize + fileByteSize);
        byteBuffer.putInt(messageType.ordinal());
        byteBuffer.putInt(idByteSize);
        byteBuffer.put(receiver.getBytes());
        byteBuffer.put(fileNameByte);
        byteBuffer.putInt(seq);
        byteBuffer.put(body);

        return byteBuffer.array();
    }

    public static byte[] getTestFileHeaderVerServer(MessageType messageType, byte[] body) {
        int bodyLengthByteSize = 4;
        int typeByteSize = 4;
        int bodyByteSize = body.length;

        ByteBuffer byteBuffer = ByteBuffer.allocate(bodyLengthByteSize + typeByteSize + bodyByteSize);
        System.out.println("byteBugger Length (may be same as client)" + byteBuffer.capacity());
        byteBuffer.putInt(body.length);
        byteBuffer.putInt(messageType.ordinal());
        byteBuffer.put(body);

        return byteBuffer.array();
    }

    public static byte[] getFileHeaderByCommand(MessageType type, String id, Integer seq, byte[] fileByte) {
        // body.length, type, id.length, id, file -> 1MB단위
        byte[] bodyLengthByte = intToByteArray(id.length() + fileByte.length);
        byte[] typeByte = getTypeByte(type);
        byte[] idByte = id.getBytes(StandardCharsets.UTF_8);
        byte[] idLengthByte = intToByteArray(idByte.length);
        byte[] seqByte = intToByteArray(seq);
        byte[] packet = new byte[4 + 4 + 4 + 4 + bodyLengthByte.length];

        // bodyLength
        System.arraycopy(bodyLengthByte, 0, packet, 0, 4);
        // typeLength
        System.arraycopy(typeByte, 0, packet, 4, 4);
        // idLength
        System.arraycopy(idLengthByte, 0, packet, 8, 4);
        // id
        System.arraycopy(idByte, 0, packet, 12, id.length());

        System.arraycopy(seqByte, 0, packet, 12 + id.length(), 4);
        // file
        System.arraycopy(fileByte, 0, packet, 12 + id.length() + 4, fileByte.length);

        return packet;
    }
}