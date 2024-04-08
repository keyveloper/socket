package org.example;

import lombok.Data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Data
public class CommandProcessor {
    private final Client client;
    public ArrayList<Object> extract(String command) {
        // ArrayList = [MessageTypeCode, MessageType]
        ArrayList<Object> returnArray = new ArrayList<>();
        boolean isCommandValid = false;
        if (command.startsWith("/r") || command.startsWith("/R")) {
            returnArray.add(MessageTypeCode.REGISTER_ID);
            String id = command.substring(3);
            returnArray.add(new RegisterIdType(id));
            isCommandValid = true;
        }
        if (command.startsWith("/Q") || command.startsWith("/q")) {
            returnArray.add(MessageTypeCode.FIN);
            returnArray.add(new NoContentType("FIN"));
            isCommandValid = true;
        }
        if (command.startsWith("/N") || command.startsWith("/n")) {
            returnArray.add(MessageTypeCode.CHANGE_ID);
            String id = command.substring(3);
            returnArray.add(new ChangeIdType(id));
            isCommandValid = true;
        }
        if (command.startsWith("/W") || command.startsWith("/w")) {
            // /W "receiver" comment
            returnArray.add(MessageTypeCode.WHISPER);
            int firstIdIndex = command.indexOf('"');
            int secondIdIndex = command.indexOf('"', firstIdIndex + 1);
            String receiverId = command.substring(firstIdIndex + 1, secondIdIndex);
            System.out.println("receiver Id: " + receiverId);
            returnArray.add(new WhisperType(receiverId, command.substring(secondIdIndex + 2).trim()));
            isCommandValid = true;
        }
        if (command.startsWith("/F") || command.startsWith("/f")) {
            // "/F "receiver" filePath "
            sendFile(command);
        }

        if (!command.startsWith("/")) {
            returnArray.add(MessageTypeCode.COMMENT);
            returnArray.add(new CommentType(command));
            isCommandValid = true;
        }
        if (!isCommandValid) {
            throw new IncorrectCommandException("Incorrect Command: " + command);
        }

        return returnArray;
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
