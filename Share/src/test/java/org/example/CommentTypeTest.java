package org.example;

import org.example.types.ChangeIdType;
import org.example.types.CommentType;
import org.example.types.MessageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CommentTypeTest {

    @Test
    public void convertTest() {
        // Given
        CommentType testType = new CommentType("my", "hello?");

        // When
        byte[] testTypeBytes = testType.toBytes();
        MessageType convertedType = testType.fromBytes(testTypeBytes);

        assertEquals(testType, convertedType);
    }
}
