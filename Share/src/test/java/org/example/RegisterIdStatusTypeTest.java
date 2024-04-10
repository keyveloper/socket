package org.example;


import org.example.types.MessageType;
import org.example.types.RegisterIdStatusType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterIdStatusTypeTest {
    @Test
    public void convertTest() {
        // Given
        RegisterIdStatusType test = new RegisterIdStatusType(true, "failed");

        // When
        byte[] testBytes = test.toBytes();
        MessageType convertedType = test.fromBytes(testBytes);

        // Then
        assertEquals(test, convertedType);
    }

}
