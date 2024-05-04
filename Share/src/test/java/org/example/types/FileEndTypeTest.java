package org.example.types;


import org.example.types.FileEndType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
public class FileEndTypeTest {
    @Test
    public void convertTest() {
        // Given
        UUID tokenId = UUID.randomUUID();
        FileEndType test = new FileEndType(tokenId);

        // When
        byte[] testBytes = test.toBytes();
        FileEndType convertType = FileEndType.fromBytes(testBytes);

        // Then
        assertEquals(test,convertType);

    }
}
