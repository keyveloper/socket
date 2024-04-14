package org.example.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class FileStartTypeTest {
    @Test
    public void convertTest() {
        //Given
        FileStartType testType = new FileStartType("receiver", "fileName");

        // When
        byte[] testBytes = testType.toBytes();
        MessageType convertedType = testType.fromBytes(testBytes);

        // Then
        assertEquals(testType, convertedType);
    }
}
