package org.example;

import lombok.Data;
import org.example.types.*;

import java.io.IOException;
import java.util.HashMap;

@Data
public class ClientServiceGiver implements ServiceGiver{
    private final Client client;
    private final HashMap<String, FileManager> fileManagerHashMap;

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
            case FILE_START -> {
                addFileManager((FileStartType) messageType);
                setFile((FileStartType) messageType);
            }
            case FILE -> {
                saveFile((FileType) messageType);

            }
            case FILE_END -> {
            }
            case NOTICE -> {
                readNotice((NoticeType) messageType);
            }
        }
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

    private void readNotice(NoticeType noticeType) {
        System.out.println(noticeType.getNotice());
    }


    private void addFileManager(FileStartType fileStartType) {
        String fileName = fileStartType.getFileName();
        if (fileManagerHashMap.containsKey(fileName)) {
            fileName = fileName + 1;
        }
        fileManagerHashMap.put(fileName, new FileManager(fileStartType.getFileName()));
    }

    private void setFile(FileStartType fileStartType) {
        FileManager fileManager = fileManagerHashMap.get(fileStartType.getFileName());
        fileManager.set();
    }

    private void saveFile(FileType fileType) {
        FileManager fileManger = fileManagerHashMap.get(fileType.getFileName());
        fileManger.save(fileType);
    }
}
