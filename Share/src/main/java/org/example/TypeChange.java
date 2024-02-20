package org.example;

import java.nio.ByteBuffer;

public class TypeChange {
    public static int byteArrayToIntLittleEndian(byte[] byteArray) {
        int result = 0;
        for (int i = 0; i < byteArray.length; i++) {
            result |= (byteArray[i] & 0xFF) << (8 * i);
        }
        return result;
    }
}