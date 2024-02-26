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

    public static byte[] getSendPacketByteWithHeader(MessageType type, String message){
        // byte[] + byte[] + byte[]
        byte[] bodyByte = message.getBytes();
        // body의 길이
        int bodyLength = bodyByte.length;
        // bodt의 길이를 나타내는 숫자 4byte이내
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