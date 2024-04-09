package org.example;

import org.example.types.WhisperType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mock;

class CommandProcessorTest {
    @Test
    void processWhisperCommand() {
        // Given
        Client client = mock(Client.class);
        CommandProcessor commandProcessor = new CommandProcessor(client);


        // When
        String command = "/w \"mom\" hello?";
        ProcessedObject processedObject = commandProcessor.extract(command);
        WhisperType whisperType = (WhisperType) processedObject.getMessageType();
        String receiver = whisperType.getId();
        String comment = whisperType.getComment();

        // Then
        assertEquals("mom", receiver);
        assertEquals("hello?", comment);
    }
}
