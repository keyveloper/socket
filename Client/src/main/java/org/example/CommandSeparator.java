package org.example;

import lombok.Data;

import java.util.HashMap;

@Data
public class CommandSeparator {
    private MessageTypeCode messageTypeCode;

    private HashMap<String, Object> contentMap;
    public void separate(String command) {
        if (command.startsWith("/r") || command.startsWith("/R")) {
            messageTypeCode = MessageTypeCode.REGISTER_ID;
            contentMap.put("id", command.substring(3));
            return;
        }
        if (command.startsWith("/Q") || command.startsWith("/q")) {
            messageTypeCode = MessageTypeCode.FIN;
            return;
        }
        if (command.startsWith("/N") || command.startsWith("/n")) {
            messageTypeCode = MessageTypeCode.CHANGE_ID;
            contentMap.put("newID", command.substring(3));
            return;
        }
        if (command.startsWith("/W") || command.startsWith("/w")) {
            // /W "receiver" comment
            messageTypeCode = MessageTypeCode.WHISPER;

            int firstIdIndex = command.indexOf('"');
            int secondIdIndex = command.indexOf('"', firstIdIndex + 1);

            contentMap.put("receiver", command.substring(firstIdIndex = 1, secondIdIndex));
            contentMap.put("comment", command.substring(secondIdIndex + 2).trim());
            return;
        }
        if (command.startsWith("/F") || command.startsWith("/f")) {
            messageTypeCode = MessageTypeCode.FILE;
            return;
        }

        messageTypeCode = MessageTypeCode.COMMENT;
        contentMap.put("comment", command);
    }
}
