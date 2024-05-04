package org.example;

import lombok.Data;
import org.example.types.*;

import java.util.HashMap;
import java.util.UUID;

@Data
public class ClientServiceGiver implements ServiceGiver{
    private final Client client;
    private final HashMap<UUID, FileSaver> fileSaverHashMap = new HashMap<>();
    private final ServerHandler serverHandler;

    @Override
    public void service(Message message, MessageType messageType) {
        switch (message.getMessageTypeCode()) {
            case REGISTER_STATUS -> readIdStatus((RegisterIdStatusType) messageType);

            case COMMENT -> readComment((CommentType) messageType);

            case WHISPER -> readWhisper((WhisperType) messageType);

            case CHANGE_ID -> informIdChange((ChangeIdType) messageType);

            case FILE -> fileSaveStart((FileType) messageType);

            case FILE_END -> removeFileManager((FileEndType) messageType);

            case NOTICE -> processNotice((NoticeType) messageType);

            case File_Token -> {
                FileToken fileToken = (FileToken) messageType;
                setTokenId(fileToken.getFileId(), fileToken.getTokenId());
            }
        }
    }

    private void fileSaveStart(FileType fileType) {
        // fileSaverMap = <tokenID(UUID), FileSaver>
        FileSaver fileSaver;
        if (fileSaverHashMap.containsKey(fileType.getTokenId())) {
            fileSaver = fileSaverHashMap.get(fileType.getTokenId());
        } else {
            fileSaver = new FileSaver(fileType.getSender());
            fileSaverHashMap.put(fileType.getTokenId(), fileSaver);
        }
        fileSaver.save(fileType.getSeq(), fileType.getFileByte());
    }

    private void setTokenId(UUID fileId, UUID tokenId) {
        FileSender fileSender = serverHandler.getFileSenderHashMap().get(fileId);
        fileSender.setTokenId(tokenId);
        fileSender.sendFile();
    }

    private void readIdStatus(RegisterIdStatusType statusType) {
        if (statusType.getIsSuccess()) {
            client.setIsRegister(true);
            client.setClientId(statusType.getRegisterId());
            System.out.println("id: " + statusType.getRegisterId() + " " + statusType.getNotice());
        }
        else {
            System.out.println("id: "+  statusType.getRegisterId() + " was failed to register\nreason: " + statusType.getNotice());
        }
    }

    private void readWhisper(WhisperType whisperType) {
        System.out.println("(Whisper) " + whisperType.getId() + ": " + whisperType.getComment());
    }

    private void readComment(CommentType commentType) {
        System.out.println(commentType.getSenderId() + ": " + commentType.getComment());
    }

    private void processNotice(NoticeType noticeType) {
        switch (noticeType.getNoticeCode()) {
            case COMMON, FIN -> readMessage(noticeType.getNotice());
        }
    }

    private void readMessage(String message) {
        System.out.println(message);
    }
    private void informIdChange(ChangeIdType changeIdType) {
        System.out.println(changeIdType.getOldId() + " changed ID -> " + changeIdType.getNewId());
        // inform to serverHandler
    }

    private void removeFileManager(FileEndType fileEndType) {
        System.out.println("[In client Service]\n fileEndType" + fileEndType);
        // fileSaver = {"tokeId": Saver}
        FileSaver fileSaver = fileSaverHashMap.get(fileEndType.getTokenId());
        System.out.println("fileSaver: " + fileSaver);
        fileSaver.writeSender();
        fileSaverHashMap.remove(fileEndType.getTokenId());
        System.out.println("remove key: " + fileEndType.getTokenId() + "\nmap: " + fileSaverHashMap);
    }

}
