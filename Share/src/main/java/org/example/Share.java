package org.example;

import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;
import java.net.*;

public class Share {

    public static int getPacketLength(byte[] packet){
        return packet.length;
    }

    public static byte[] getPacketLengthByte(int packLength){
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(packLength);
        return buffer.array();
    }

    public static byte[] getTypeByte(String type){
        MessageType messageType = MessageType.valueOf(type.toUpperCase());
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(messageType.ordinal());
        return buffer.array();
    }

    public static byte[] getHeader(byte[] packet, String type){
        int packetLength = getPacketLength(packet);
        byte[] packetLengthByte = getPacketLengthByte(packetLength);
        byte[] packetTypeByte = getTypeByte(type);

        ByteBuffer header = ByteBuffer.allocate(8);
        header.put(packetLengthByte);
        header.put(packetTypeByte);

        return header.array();
    }

}

// 이미 public
enum MessageType{
    COMMENT,
    NOTICE,
    DIRECT,
    ALARM,
    REGISTER_ID,
    END
}

