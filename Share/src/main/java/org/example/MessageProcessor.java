package org.example;

import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

@Data
public class MessageProcessor {
    // read Message
    // make Type
    public static MessageType makeMessageType(Message message) {
        int bodyLength = message.getBodyLength();
        MessageTypeCode messageTypeCode = message.getMessageTypeCode();
        byte[] body = message.getBody();
        System.out.println("inMessageProcessor: " + "\nbodyLength: " + bodyLength + "\nmessageTypeCode: " + messageTypeCode + "\nbody: " + Arrays.toString(body));
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