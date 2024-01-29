package org.example;

import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;
import java.net.*;

public class Share {

    public static byte[] getTypeByte(String type){
        MessageType messageType = MessageType.valueOf(type.toUpperCase());
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(messageType.ordinal());
        return buffer.array();
    }

    public static byte[] getPacketLengthByte(int packLength){
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(packLength);
        return buffer.array();
    }

    public static byte[] plusHeader(byte[] packet, String type){
        System.out.println("start plus header packet, type " + new String(packet) + " " + type);

        // byte[] + byte[] + byte[]
        int packetLength = packet.length + 8;
        byte[] packetLengthByte = getPacketLengthByte(packetLength);
        byte[] packetTypeByte = getTypeByte(type.toUpperCase());

        byte[] headerPacketByte = new byte[packetLengthByte.length + packetTypeByte.length + packet.length];
        System.arraycopy(packetLengthByte, 0, headerPacketByte, 0, packetLengthByte.length);
        System.arraycopy(packetTypeByte, 0, headerPacketByte, packetLengthByte.length, packetTypeByte.length);
        System.arraycopy(packet, 0, headerPacketByte, packetLengthByte.length + packetTypeByte.length, packet.length);

        System.out.print("Header plus complete, message = " + new String(headerPacketByte) );
        return headerPacketByte;
    }

}

//public
enum MessageType{
    REGISTER_ID,
    COMMENT,
    END
}

