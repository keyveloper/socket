package org.example;

import lombok.Data;

import java.io.Serializable;

@Data
public class NoticeType implements Serializable, MessageType {
    private final String notice;
}
