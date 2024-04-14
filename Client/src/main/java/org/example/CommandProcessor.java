package org.example;

import lombok.Data;
import org.example.types.*;

@Data
public class CommandProcessor {
    private final Client client;
    public ProcessedObject extract(String command, boolean isRegister) {
        if (command.startsWith("/r") || command.startsWith("/R")) {
            String id = command.substring(3);
            return new ProcessedObject(MessageTypeCode.REGISTER_ID, new RegisterIdType(id));
        }

        if (!isRegister) {
            throw new NotRegisterException();
        }

        if (command.startsWith("/Q") || command.startsWith("/q")) {
            return new ProcessedObject(MessageTypeCode.FIN, new NoticeType(NoticeCode.FIN,""));
        }
        if (command.startsWith("/N") || command.startsWith("/n")) {
            String newId = command.substring(3);
            return new ProcessedObject(MessageTypeCode.CHANGE_ID, new ChangeIdType(client.getClientId(), newId));
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
            // file을 연속적으로 뽑아내서 보내야 하는데?....
            int firstIdIndex = command.indexOf('"');
            int secondIdIndex = command.indexOf('"', firstIdIndex + 1);
            int filePathStartIndex = secondIdIndex + 1;
            System.out.println("filePathStartIndex: " + filePathStartIndex);
            int lastBackSlashIndex = command.lastIndexOf("\\");
            int extensionStartIndex = command.lastIndexOf(".");

            // "/F "receiver" filePath "
            String receiverId = command.substring(firstIdIndex + 1, secondIdIndex);
            System.out.println("receiverId: " + receiverId);
            String filePath = command.substring(filePathStartIndex + 1);
            System.out.println("filePath: " + filePath);

            String fileName = command.substring(lastBackSlashIndex + 1, extensionStartIndex);
            FileStartType fileStartType = new FileStartType(receiverId, fileName);
            fileStartType.setFilePath(filePath);
            return new ProcessedObject(MessageTypeCode.FILE_START, fileStartType);

        }

        if (!command.startsWith("/")) {
            return new ProcessedObject(MessageTypeCode.COMMENT, new CommentType(client.getClientId(),command));
        }
        throw new IncorrectCommandException("Incorrect Command: " + command);
    }


}
