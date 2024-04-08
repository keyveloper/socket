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
        MessageTypeCode messageTypeCode = message.getMessageTypeCode();
        byte[] body = message.getBody();
        MessageType messageType = null;
        try{
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            switch (messageTypeCode) {
                case REGISTER_ID:
                    messageType = (RegisterIdType) objectInputStream.readObject();
                    break;
                case REGISTER_STATUS:
                    messageType = (RegisterIdStatusType) objectInputStream.readObject();
                    break;
                case WHISPER:
                    messageType = (WhisperType) objectInputStream.readObject();
                    break;
                case FILE:
                    messageType = (FileType) objectInputStream.readObject();
                    break;
                case COMMENT:
                    messageType = (CommentType) objectInputStream.readObject();
                    break;
                case NOTICE:
                    messageType = (NoticeType) objectInputStream.readObject();
                    break;
                case FIN:
                    messageType = (NoContentType) objectInputStream.readObject();
                    break;
                case CHANGE_ID:
                    messageType = (ChangeIdType) objectInputStream.readObject();
                    break;
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return messageType;
    }
}