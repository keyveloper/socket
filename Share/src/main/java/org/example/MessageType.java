package org.example;

import lombok.Data;


public interface MessageType{
    byte[] toBytes();

    MessageType fromBytes(byte[] bytes);
}
