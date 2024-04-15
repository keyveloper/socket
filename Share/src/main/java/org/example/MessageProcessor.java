package org.example;

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
            case REGISTER_ID -> {
                RegisterIdType registerDecoder = new RegisterIdType("decoder");
                yield registerDecoder.fromBytes(body);
            }
            case REGISTER_STATUS -> {
                RegisterIdStatusType statusDecoder = new RegisterIdStatusType(true, "","decoder");
                yield statusDecoder.fromBytes(body);
            }
            case WHISPER -> {
                WhisperType whisperDecoder = new WhisperType("decoder", "decoder");
                yield whisperDecoder.fromBytes(body);
            }
            case FILE_START -> {
                FileStartType fileStartDecoder = new FileStartType("decoder", "decoder");
                yield fileStartDecoder.fromBytes(body);
            }
            case FILE -> {
                FileType fileDecoder = new FileType(true, "decoder", "decoder", 0, null);
                yield fileDecoder.fromBytes(body);
            }
            case FILE_END -> {
                FileEndType fileEndDecoder = new FileEndType("decoder", "decoder");
                yield fileEndDecoder.fromBytes(body);
            }
            case COMMENT -> {
                CommentType commentDecoder = new CommentType("decoder", "decoder");
                yield commentDecoder.fromBytes(body);
            }
            case NOTICE -> {
                NoticeType noticeDecoder = new NoticeType(NoticeCode.COMMON, "decoder");
                yield noticeDecoder.fromBytes(body);
            }
            case FIN -> {
                NoticeType finDecoder = new NoticeType(NoticeCode.FIN,"decoder");
                yield finDecoder.fromBytes(body);
            }
            case CHANGE_ID -> {
                ChangeIdType changeIdDecoder = new ChangeIdType("decoder", "decoder");
                yield changeIdDecoder.fromBytes(body);
            }

            default -> null;
        };
    }
}