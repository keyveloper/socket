package org.example;

import lombok.Data;
import org.example.types.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

@Data
public class CommandProcessor {
    private final Client client;
    public ProcessedObject extract(String command) {
        // ArrayList = [MessageTypeCode, MessageType]
        if (command.startsWith("/r") || command.startsWith("/R")) {
            String id = command.substring(3);
            return new ProcessedObject(MessageTypeCode.REGISTER_ID, new RegisterIdType(id));
        }
        if (command.startsWith("/Q") || command.startsWith("/q")) {
            return new ProcessedObject(MessageTypeCode.FIN, new NoticeType("FIN"));
        }
        if (command.startsWith("/N") || command.startsWith("/n")) {
            String newId = command.substring(3);
            return new ProcessedObject(MessageTypeCode.CHANGE_ID, new ChangeIdType(newId));
        }
        if (command.startsWith("/W") || command.startsWith("/w")) {
            // /W "receiver" comment
            int firstIdIndex = command.indexOf('"');
            int secondIdIndex = command.indexOf('"', firstIdIndex + 1);
            String receiverId = command.substring(firstIdIndex + 1, secondIdIndex);
            String comment = command.substring(secondIdIndex +2);

            System.out.println("In Process command: /w\nreceiverId" + receiverId + "\ncomment: " + command);
            return new ProcessedObject(MessageTypeCode.WHISPER, new WhisperType(receiverId, comment));
        }
        if (command.startsWith("/F") || command.startsWith("/f")) {
            // "/F "receiver" filePath "
            sendFile(command);
        }

        if (!command.startsWith("/")) {
            return new ProcessedObject(MessageTypeCode.COMMENT, new CommentType(client.getClientId(),command));
        }
        throw new IncorrectCommandException("Incorrect Command: " + command);
    }

    private void sendFile(String command) {
        int firstIdIndex = command.indexOf('"');
        int secondIdIndex = command.indexOf('"', firstIdIndex + 1);
        int filePathStartIndex = secondIdIndex + 1;
        System.out.println("filePathStartIndex: " + filePathStartIndex);
        int lastBackSlashIndex = command.lastIndexOf("\\");
        int extensionStartIndex = command.lastIndexOf(".");

        String receiverId = command.substring(firstIdIndex + 1, secondIdIndex);
        System.out.println("receiverId: " + receiverId);
        String filePath = command.substring(filePathStartIndex + 1);
        System.out.println("filePath: " + filePath);

        String fileName = command.substring(lastBackSlashIndex + 1, extensionStartIndex);

        System.out.println("receiverId : " + receiverId + "\nfilePath: " + filePath + "\nfileName: " + fileName);
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            byte[] fileBuffer = new byte[1024 * 1024]; // 1MB read per once
            int bytesRead;
            int seq = 0;
            while ((bytesRead = fileInputStream.read(fileBuffer)) != -1) {
                byte[] actualRead = Arrays.copyOf(fileBuffer, bytesRead);

                FileType fileType = new FileType(false, receiverId, fileName, seq, actualRead);
                seq += 1;
                client.fileSend(fileType);
            }
            // end -> true
            FileType endFileType = new FileType(true, receiverId, fileName, -1, null);
            client.fileSend(endFileType);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
