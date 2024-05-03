package org.example.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class CommentType implements MessageType {
    private final String senderId;
    private final String comment;
}
