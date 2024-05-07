package org.example;

import lombok.Data;
import org.example.types.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

@Data
public class CommandProcessor {
    private final Client client;
    public ProcessedObject extract(String command, boolean isRegister) {
        if (command.startsWith("/r") || command.startsWith("/R")) {
            if (command.substring(3).isEmpty()) {
                return null;
            }
            String id = command.substring(3);
            return new ProcessedObject(MessageTypeCode.REGISTER_ID, new RegisterIdType(id));
        }

        if (!isRegister) {
            return null;
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

            return new ProcessedObject(MessageTypeCode.WHISPER, new WhisperType(receiverId, comment));
        }
        if (command.startsWith("/F") || command.startsWith("/f")) {
            // file을 연속적으로 뽑아내서 보내야 하는데?....
            int firstIdIndex = command.indexOf('"');
            int secondIdIndex = command.indexOf('"', firstIdIndex + 1);
            int filePathStartIndex = secondIdIndex + 1;

            // "/F "receiver" filePath "
            String receiverId = command.substring(firstIdIndex + 1, secondIdIndex);
            System.out.println("receiverId: " + receiverId + "\n");
            String filePath = command.substring(filePathStartIndex + 1);
            System.out.println("filePath: " + filePath + "\n");

            UUID fileId = UUID.randomUUID();
            System.out.println("fileId: " + fileId + "\n");

            FileStartInfo fileStartInfo = new FileStartInfo(client.getClientId(), receiverId, filePath, fileId);
            System.out.println("fileStartInfo: " + fileStartInfo);
            return new ProcessedObject(MessageTypeCode.File_START_INFO, fileStartInfo);

        }

        if (!command.startsWith("/")) {
            return new ProcessedObject(MessageTypeCode.COMMENT, new CommentType(client.getClientId(),command));
        }
        throw new IncorrectCommandException("Incorrect Command: " + command);
    }


}
