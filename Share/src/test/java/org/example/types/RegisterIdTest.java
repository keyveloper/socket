package org.example.types;

import org.example.types.MessageType;
import org.example.types.RegisterIdType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterIdTest {
    @Test
    public void convert() {
        // Given
        RegisterIdType test = new RegisterIdType("dd");

        // When
        byte[] testBytes = test.toBytes();
        MessageType convertedType = test.fromBytes(testBytes);

        // Then
        assertEquals(test, convertedType);
    }
}
