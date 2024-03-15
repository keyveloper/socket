package org.example;

import java.nio.*;
import java.nio.charset.StandardCharsets;

import static org.example.Share.*;
import static org.example.TypeChange.intToByteArray;

public class FileProcessor {
    public static String getReceiverId(byte[] body) {
        int receiverIdLength = ByteBuffer.wrap(body, 0, 4).getInt();
        String receiverId = new String(body, 4, receiverIdLength);
        return receiverId;
    }

    public static Integer getFileSeq(byte[] body) {
        int receiverIdLength = ByteBuffer.wrap(body, 0, 4).getInt();
        String receiverId = new String(body, 4, receiverIdLength);

        int seq = ByteBuffer.wrap(body, 8 + receiverIdLength, 4).getInt();
        return seq;
    }

    public static byte[] getFileByte(byte[] body) {
        int receiverIdLength = ByteBuffer.wrap(body, 0, 4).getInt();
        String receiverId = new String(body, 4, receiverIdLength);
        int seq = ByteBuffer.wrap(body, 8 + receiverIdLength, 4).getInt();
        byte[] fileByte = ByteBuffer.wrap(body, 12 + receiverIdLength, body.length - 4 - receiverIdLength - 4).array();

        return fileByte;
    }

    public static byte[] getFileHeader(String id, Integer seq, byte[] fileByte) {
        // body.length, type, id.length, id, file -> 1MB단위
        byte[] bodyLengthByte = intToByteArray( id.length() + fileByte.length);
        byte[] typeByte = getTypeByte(MessageType.FILE);
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

    public static byte[] getFileEndHeader(String id, Integer seq, byte[] fileByte) {
        // body.length, type, id.length, id, file -> 1MB단위
        byte[] bodyLengthByte = intToByteArray( id.length() + fileByte.length);
        byte[] typeByte = getTypeByte(MessageType.FILE_END);
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
