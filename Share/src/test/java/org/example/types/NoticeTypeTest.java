package org.example.types;

import org.example.NoticeCode;
import org.example.types.MessageType;
import org.example.types.NoticeType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NoticeTypeTest {
    @Test
    public void convert() {
        // Given
        NoticeType test = new NoticeType(NoticeCode.COMMON,"notice");

        // When
        byte[] testBytes = test.toBytes();
        MessageType convertedType = NoticeType.fromBytes(testBytes);

        // Then
        assertEquals(test, convertedType);

    }
}
