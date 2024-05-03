package org.example.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
;
import java.nio.ByteBuffer;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class WhisperType implements MessageType {
    // id = receiver or sender
    private final String id;
    private final String comment;
}
