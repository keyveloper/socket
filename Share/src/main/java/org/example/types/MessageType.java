package org.example.types;


public interface MessageType{

    byte[] toBytes();

    MessageType fromBytes(byte[] bytes);
}
