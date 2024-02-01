package org.example;

// 형 변환을 돕는 클래스
public class TypeChange {
    public static int byteArrayToIntLittleEndian(byte[] byteArray) {
        // 리틀 엔디안 방식으로 변환
        int result = 0;
        for (int i = 0; i < byteArray.length; i++) {
            result |= (byteArray[i] & 0xFF) << (8 * i);
        }
        return result;
    }
}
