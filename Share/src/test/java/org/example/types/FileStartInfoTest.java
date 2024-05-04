package org.example.types;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileStartInfoTest {
    @Test
    public void convertFileTest() {
        // Given
        UUID fileId = UUID.randomUUID();
        FileStartInfo fileStartInfo = new FileStartInfo("sender", "receiver", "filePath", fileId);
        // When
        byte[] testTypeByte = fileStartInfo.toBytes();
        MessageType convertedType = FileStartInfo.fromBytes(testTypeByte);

        // Then
        assertEquals(testTypeByte, convertedType);
    }
}
