package org.example.types;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

public class FileTypeTest {
    @Test
    public void convertFileTest() {
        // Given
        byte[] fileByte = {101};
        FileType testType = new FileType("sender", "receiver", "fileName", 0, fileByte);

        // When
        byte[] testTypeByte = testType.toBytes();
        MessageType convertedType = testType.fromBytes(testTypeByte);

        // Then
        assertEquals(testType, convertedType);

    }
}
