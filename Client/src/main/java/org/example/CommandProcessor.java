package org.example;

import lombok.Data;

@Data
public class CommandProcessor {
    private final String command;

    private MessageType messageType;

    private void makeMessageType() {
        if (command.startsWith("/r") || command.startsWith("/R")) {
            return;
        }
        if (command.startsWith("/Q") || command.startsWith("/q")) {
            return;
        }
        if (command.startsWith("/N") || command.startsWith("/n")) {
            return;
        }
        if (command.startsWith("/W") || command.startsWith("/w")) {
            return;
        }
        if (command.startsWith("/F") || command.startsWith("/f")) {
            return;
        }
    }
}
