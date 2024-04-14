package org.example.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
<<<<<<< HEAD
public class FileStartTypeTest{
=======
public class FileStartTypeTest {
>>>>>>> eee515ed7a3cb19ef0b7276a1af5fb1c4b1c4470
    @Test
    public void convertTest() {
        //Given
        FileStartType testType = new FileStartType("receiver", "fileName");

        // When
        byte[] testBytes = testType.toBytes();
        MessageType convertedType = testType.fromBytes(testBytes);

        // Then
        assertEquals(testType, convertedType);
    }
}
