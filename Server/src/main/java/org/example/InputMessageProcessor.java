package org.example;

import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Data
public class InputMessageProcessor {
    // read Message
    // make Type
    public MessageType makeType(Message message) {
        int bodyLength = message.getBodyLength();
        MessageTypeCode messageTypeCode = message.getMessageType();
        byte[] body = new byte[bodyLength];
        MessageType messageType = null;
        try{
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            switch (messageTypeCode) {
                case REGISTER_ID:
                    messageType = (RegisterIdType) objectInputStream.readObject();
                    break;
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return messageType;
    }

}
