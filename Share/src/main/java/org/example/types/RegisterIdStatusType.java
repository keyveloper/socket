package org.example.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RegisterIdStatusType implements MessageType {
    private final Boolean isSuccess;
    private final String registerId;
    private final String notice;
}
