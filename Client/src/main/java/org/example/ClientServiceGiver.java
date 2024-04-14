package org.example;

import lombok.Data;
import lombok.ToString;
import org.example.types.*;

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
                System.out.println("[IN ServiceGiver] File_START");
                addFileManager((FileStartType) messageType);
                setFile((FileStartType) messageType);
            }
            case FILE -> {
                saveFile((FileType) messageType);
            }
            case FILE_END -> {
                removeFileManager((FileEndType) messageType);
            }
            case NOTICE -> {
                processNotice((NoticeType) messageType);
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

    private void processNotice(NoticeType noticeType) {
        switch (noticeType.getNoticeCode()) {
            case COMMON, FIN -> {
                readMessage(noticeType.getNotice());
            }
            case ID_CHANGE -> {
                informIdChange();
            }
        }
    }

    private void readMessage(String message) {
        System.out.println(message);
    }
    private void informIdChange() {

    }


    private void addFileManager(FileStartType fileStartType) {
        System.out.println("start add FIle manger");
        String fileName = fileStartType.getFileName();
        if (fileManagerHashMap.containsKey(fileName)) {
            fileName = fileName + 1;
        }
        fileManagerHashMap.put(fileName, new FileManager(fileStartType.getFileName(), fileStartType.getId()));
        System.out.println("new FileManger added");
    }

    private void setFile(FileStartType fileStartType) {
        System.out.println("start set file");
        FileManager fileManager = fileManagerHashMap.get(fileStartType.getFileName());
        fileManager.set();
    }

    private void saveFile(FileType fileType) {
        System.out.println("start save FIle");
        FileManager fileManger = fileManagerHashMap.get(fileType.getFileName());
        fileManger.save(fileType);
    }

    private void removeFileManager(FileEndType fileEndType) {
        FileManager fileManger = fileManagerHashMap.get(fileEndType.getFileName());
        fileManger.writeSender();
        fileManagerHashMap.remove(fileEndType.getFileName());
        System.out.println("remove key: " + fileEndType.getFileName() + "\nmap: " + fileManagerHashMap);
    }

}
