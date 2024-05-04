package org.example.types;

import org.example.types.MessageType;
import org.example.types.WhisperType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class WhisperTypeTest {
    @Test
    public void convertTest() {
        // Given
        WhisperType testType = new WhisperType("id", "hellow?");

        // When
        byte[] testTypeByte = testType.toBytes();
        MessageType convertedType = WhisperType.fromBytes(testTypeByte);

        // Then
        assertEquals(testType, convertedType);
    }
}
