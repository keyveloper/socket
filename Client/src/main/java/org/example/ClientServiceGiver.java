package org.example;

import lombok.Data;

import java.io.IOException;

@Data
public class ClientServiceGiver implements ServiceGiver{
    private final Client client;
    @Override
    public void service(Message message, MessageType messageType) throws IOException {
        switch (message.getMessageTypeCode()) {
            case REGISTER_STATUS:
                readIdStatus((RegisterIdStatusType) messageType);
                break;
            case WHISPER:
                readWhisper((WhisperType) messageType);
                break;
            case COMMENT:
                readComment((CommentType) messageType);
                break;
            case NOTICE:
                readNotice((NoticeType) messageType);
                break;
        }
    }

    private void readIdStatus(RegisterIdStatusType statusType) {
        if (statusType.getIsSuccess()) {
            client.setIsRegister(true);
            System.out.println(statusType.getNotice());
        }
        else {
            System.out.println("Register Failed\nreason: " + statusType.getNotice());
        }
    }

    private void readWhisper(WhisperType whisperType) {
        System.out.println("(Whisper) " + whisperType.getId() + ": " + whisperType.getComment());
    }

    private void readComment(CommentType commentType) {
        System.out.println(commentType.getComment());
    }

    private void readNotice(NoticeType noticeType) {
        System.out.println(noticeType.getNotice());
    }

}
