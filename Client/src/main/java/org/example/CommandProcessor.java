package org.example;

import lombok.Data;

import java.util.ArrayList;

@Data
public class CommandProcessor {
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

            returnArray.add(new WhisperType(command.substring(firstIdIndex, secondIdIndex), command.substring(secondIdIndex + 2).trim()));
            isCommandValid = true;
        }
        if (command.startsWith("/F") || command.startsWith("/f")) {
            returnArray.add(MessageTypeCode.FILE);
            int firstIdIndex = command.indexOf('"');
            // int secondIdIndex = command.indexOf('"', firstIdIndex + 1);

            // file 만들고 넘겨주기
            // returnArray.add(new FileType(command.substring(firstIdIndex = 1, secondIdIndex), ))
            isCommandValid = true;
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
}
