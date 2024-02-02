package org.example;

import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;
import java.net.*;

public class Share {

    public static byte[] getTypeByte(MessageType messageType){
        int typeInt = messageType.ordinal();
        System.out.println("type int : " + typeInt);
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

    public static byte[] getPacketLengthByte(int packLength){
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(packLength);
        return buffer.array();
    }

    public static byte[] getHeaderPacketByte(byte[] packet, MessageType type){
        System.out.println("start plus header packet, type " + new String(packet) + " " + type);

        // byte[] + byte[] + byte[]
        int packetLength = packet.length;
        byte[] packetLengthByte = getPacketLengthByte(packetLength);
        byte[] packetTypeByte = getTypeByte(type);

        byte[] headerPacketByte = new byte[packetLengthByte.length + packetTypeByte.length + packet.length];
        System.arraycopy(packetLengthByte, 0, headerPacketByte, 0, packetLengthByte.length);
        System.arraycopy(packetTypeByte, 0, headerPacketByte, packetLengthByte.length, packetTypeByte.length);
        System.arraycopy(packet, 0, headerPacketByte, packetLengthByte.length + packetTypeByte.length, packet.length);

        System.out.print("Header plus complete, message = " + new String(headerPacketByte) );
        return headerPacketByte;
    }

}




