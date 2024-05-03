package org.example.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ChangeIdType implements MessageType {
    private final String oldId;
    private final String newId;
}