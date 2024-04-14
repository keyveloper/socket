package org.example.types;


import org.example.types.FileEndType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class FileEndTypeTest {
    @Test
    public void convertTest() {
        // Given
        FileEndType test = new FileEndType("id", "rr");

        // When
        byte[] testBytes = test.toBytes();
        FileEndType convertType = test.fromBytes(testBytes);

        // Then
        assertEquals(test,convertType);

    }
}
