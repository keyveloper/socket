package org.example.types;

import org.example.types.ChangeIdType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class ChangeIdTypeTest {
    @Test
    public void convertTest() {
        // Given
        ChangeIdType testType = new ChangeIdType("myId");

        // When
        byte[] changeIdTypeByte = testType.toBytes();
        ChangeIdType convertedType = (ChangeIdType) testType.fromBytes(changeIdTypeByte);
        // Then

        assertEquals(testType, convertedType);

    }

}
