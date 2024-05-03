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
            case REGISTER_ID ->  objectMapper.readValue(new String(body), RegisterIdType.class);

            case REGISTER_STATUS -> objectMapper.readValue(new String(body), RegisterIdStatusType.class);

            case WHISPER -> objectMapper.readValue(new String(body), WhisperType.class);

            case FILE -> objectMapper.readValue(new String(body), FileType.class);

            case FILE_END -> objectMapper.readValue(new String(body), FileEndType.class);

            case COMMENT -> objectMapper.readValue(new String(body), CommentType.class);

            case NOTICE -> objectMapper.readValue(new String(body), NoticeType.class);

            case FIN -> objectMapper.readValue(new String(body), NoContentType.class);

            case CHANGE_ID -> objectMapper.readValue(new String(body), ChangeIdType.class);


            default -> null;
        };
    }
}