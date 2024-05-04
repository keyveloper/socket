package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import org.example.types.*;


@Data
public class MessageProcessor {
    // read Message
    // make Type
    public static MessageType makeMessageType(Message message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] body = message.getBody();
        return switch (message.getMessageTypeCode()) {
            // body -> decode -> MessageType
            case REGISTER_ID ->  RegisterIdType.fromBytes(body);

            case REGISTER_STATUS -> RegisterIdStatusType.fromBytes(body);

            case WHISPER -> WhisperType.fromBytes(body);

            case FILE -> FileType.fromBytes(body);

            case FILE_END -> FileEndType.fromBytes(body);

            case COMMENT -> CommentType.fromBytes(body);

            case NOTICE -> NoticeType.fromBytes(body);

            case FIN -> NoContentType.fromBytes(body);

            case CHANGE_ID -> ChangeIdType.fromBytes(body);

            default -> null;
        };
    }
}