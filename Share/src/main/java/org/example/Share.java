package org.example;

import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;
import java.net.*;

public class Share {
    public static int portNum = 9999;
    public static byte[] getTypeByte(MessageType messageType){
        int typeInt = messageType.ordinal();
        byte[] typeBytes = intToByteArray(typeInt);

        return typeBytes;
    }

    private static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static byte[] getPacketHeader(MessageType type, String message){
        // byte[] + byte[] + byte[]
        byte[] bodyByte = message.getBytes();
        // body의 길이
        int bodyLength = bodyByte.length;
        // body의 길이를 나타내는 숫자 4byte이내
        byte[] bodyLengthByte = intToByteArray(bodyLength);
        byte[] packetTypeByte = getTypeByte(type);

        byte[] packet = new byte[4 + 4 + bodyLength];

        // bodyLengthByte 복사
        System.arraycopy(bodyLengthByte, 0, packet, 0, 4);

        // packetTypeByte 복사
        System.arraycopy(packetTypeByte, 0, packet, 4, 4);

        // bodyByte 복사
        System.arraycopy(bodyByte, 0, packet, 8, bodyLength);

        return packet;
    }

    public static byte[] getFilePacketHeader(String id, byte[] fileByte) {
        // body.length, type, id.length, id, file -> 1MB단위
        byte[] bodyLengthByte = intToByteArray( id.length() + fileByte.length);
        byte[] typeByte = getTypeByte(MessageType.FILE);

        byte[] idByte = id.getBytes();
        byte[] idLengthByte = intToByteArray(idByte.length);
        byte[] packet = new byte[4 + 4 + 4 + bodyLengthByte.length];

        // bodyLength
        System.arraycopy(bodyLengthByte, 0, packet, 0, 4);
        // typeLength
        System.arraycopy(typeByte, 0, packet, 4, 4);
        // idLength
        System.arraycopy(idLengthByte, 0, packet, 8, 4);
        // id
        System.arraycopy(idByte, 0, packet, 12, id.length());
        // file
        System.arraycopy(fileByte, 0, packet, 12 + id.length(), fileByte.length);

        return packet;
    }

    public static String readInputMessage(byte[] packet){
        String message = new String(packet);
        return message;
    }

    public static MessageType readInputType(byte[] packet){
        MessageType type;
        ByteBuffer typeBuffer = ByteBuffer.wrap(packet);
        int typInt = typeBuffer.getInt();
        type = MessageType.values()[typInt];
        return type;
    }

    public static Integer readInputLength(byte[] packet){
        int packetLength = ByteBuffer.wrap(packet).getInt();
        return packetLength;
    }

}