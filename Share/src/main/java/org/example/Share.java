package org.example;

import java.nio.*;

import static org.example.TypeChange.intToByteArray;

public class Share {
    public static int portNum = 9999;
    public static byte[] getTypeByte(MessageType messageType){
        int typeInt = messageType.ordinal();
        byte[] typeBytes = intToByteArray(typeInt);

        return typeBytes;
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

    public static byte[] getPacketHeaderVerByte(MessageType type, byte[] body) {
        byte[] bodyLengthByte = intToByteArray(body.length);
        byte[] packetTypeByte = getTypeByte(type);
        byte[] packet = new byte[4 + 4 + body.length];

        System.arraycopy(bodyLengthByte, 0, packet, 0, 4);
        System.arraycopy(packetTypeByte, 0, packet, 4, 4);
        System.arraycopy(body, 0, packet, 8, body.length);

        return packet;
    }

    public static String readInputMessage(byte[] packet){
        return new String(packet);
    }


    public static Integer readInputLength(byte[] packet){
        int packetLength = ByteBuffer.wrap(packet).getInt();
        return packetLength;
    }

}