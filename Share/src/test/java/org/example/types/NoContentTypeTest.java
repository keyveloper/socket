package org.example.types;

import org.example.NoticeCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoContentTypeTest {
    @Test
    public void convert() {
        // Given
        NoContentType test = new NoContentType("test");

        // When
        byte[] testBytes = test.toBytes();
        MessageType convertedType = NoContentType.fromBytes(testBytes);

        // Then
        assertEquals(test, convertedType);

    }
}
