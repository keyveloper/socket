package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;

import org.example.types.*;


@Data
public class MessageProcessor {
    // read Message
    // make Type
    public static MessageType makeMessageType(Message message) {
        byte[] body = message.getBody();

        return switch (message.getMessageTypeCode()) {
            // body -> decode -> MessageType
            case REGISTER_ID ->  RegisterIdType.fromBytes(body);

            case REGISTER_STATUS -> RegisterIdStatusType.fromBytes(body);

            case WHISPER -> WhisperType.fromBytes(body);

            case File_START_INFO -> FileStartInfo.fromBytes(body);

            case File_Token -> FileToken.fromBytes(body);

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