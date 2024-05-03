package org.example.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class FileEndType implements MessageType{
    // id = receiver, sender
    private final String id;
    private final String fileName;
}
