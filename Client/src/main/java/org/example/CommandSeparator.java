package org.example;

import lombok.Data;

import java.util.HashMap;

@Data
public class CommandSeparator {
    private final String command;

    private MessageType messageType;

    private HashMap<String, Object> contentMap;
    private void separate() {
        if (command.startsWith("/r") || command.startsWith("/R")) {
            messageType = MessageType.REGISTER_ID;
            contentMap.put("id", command.substring(3));
            return;
        }
        if (command.startsWith("/Q") || command.startsWith("/q")) {
            messageType = MessageType.FIN;
            return;
        }
        if (command.startsWith("/N") || command.startsWith("/n")) {
            messageType = MessageType.CHANGE_ID;
            contentMap.put("newID", command.substring(3));
            return;
        }
        if (command.startsWith("/W") || command.startsWith("/w")) {
            // /W "receiver" comment
            messageType = MessageType.WHISPER;

            int firstIdIndex = command.indexOf('"');
            int secondIdIndex = command.indexOf('"', firstIdIndex + 1);

            contentMap.put("reciever", command.substring(firstIdIndex = 1, secondIdIndex));
            contentMap.put("comment", command.substring(secondIdIndex + 2).trim());
        }
        if (command.startsWith("/F") || command.startsWith("/f")) {
            messageType = MessageType.FILE;
        }
    }
}
