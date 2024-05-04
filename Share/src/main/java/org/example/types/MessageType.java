package org.example.types;


public interface MessageType{
    byte[] toBytes();

    static MessageType fromBytes(byte[] bytes) {
        return null;
    }


}
