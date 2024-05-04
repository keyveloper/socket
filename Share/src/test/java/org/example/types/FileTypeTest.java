package org.example.types;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.UUID;

public class FileTypeTest {
    @Test
    public void convertFileTest() {
        // Given
        byte[] fileByte = {101};
        UUID tokenId = UUID.randomUUID();
        FileType testType = new FileType("sender", tokenId, 0, fileByte);

        // When
        byte[] testTypeByte = testType.toBytes();
        MessageType convertedType = FileType.fromBytes(testTypeByte);

        // Then
        assertEquals(testType, convertedType);

    }
}
