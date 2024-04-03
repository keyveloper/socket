package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentType implements Serializable, MessageType {
    private final String comment;
}
