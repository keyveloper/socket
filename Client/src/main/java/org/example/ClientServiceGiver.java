package org.example;

import lombok.Data;
import org.example.types.*;

import java.util.HashMap;

@Data
public class ClientServiceGiver implements ServiceGiver{
    private final Client client;
    private final HashMap<String, FileSaver> fileSaverHashMap = new HashMap<>();

    @Override
    public void service(Message message, MessageType messageType) {
        switch (message.getMessageTypeCode()) {
            case REGISTER_STATUS -> {
                readIdStatus((RegisterIdStatusType) messageType);
            }
            case COMMENT -> {
                readComment((CommentType) messageType);
            }
            case WHISPER -> {
                readWhisper((WhisperType) messageType);
            }

            case CHANGE_ID -> {
                informIdChange((ChangeIdType) messageType);
            }
            case FILE -> {
                fileSaveStart((FileType) messageType);
            }
            case FILE_END -> {
                removeFileManager((FileEndType) messageType);
            }
            case NOTICE -> {
                processNotice((NoticeType) messageType);
            }
        }
    }

    private void fileSaveStart(FileType fileType) {
        String fileMangerKey = addFileSaver(fileType.getFileName(), fileType.getSender());
        FileSaver fileSaver = fileSaverHashMap.get(fileMangerKey);
        fileSaver.set();
        fileSaver.save(fileType.getSeq(), fileType.getFileByte());
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
            case COMMON, FIN -> {
                readMessage(noticeType.getNotice());
            }
        }
    }

    private void readMessage(String message) {
        System.out.println(message);
    }
    private void informIdChange(ChangeIdType changeIdType) {
        System.out.println(changeIdType.getOldId() + " changed ID -> " + changeIdType.getNewId());

        // inform to serverHandler
        if (client.getServerHandler().checkFileSender(changeIdType.getOldId())) {
            client.getServerHandler().informReceiverChange(changeIdType.getOldId(),changeIdType.getNewId());
        }
    }


    private String addFileSaver(String fileName, String sender) {
        System.out.println("start add FIle manger");
        if (fileSaverHashMap.containsKey(fileName)) {
            fileName = fileName + 1;
        }
        fileSaverHashMap.put(fileName, new FileSaver(sender));
        System.out.println("new fileManger added: " + fileSaverHashMap);
        return fileName;
    }

    private void removeFileManager(FileEndType fileEndType) {
        FileSaver fileManger = fileSaverHashMap.get(fileEndType.getFileName());
        fileManger.writeSender();
        fileSaverHashMap.remove(fileEndType.getFileName());
        System.out.println("remove key: " + fileEndType.getFileName() + "\nmap: " + fileSaverHashMap);
    }

}
