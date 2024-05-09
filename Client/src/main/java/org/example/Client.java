package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.types.FileStartInfo;
import org.example.types.FileType;
import org.example.types.MessageTypeCode;
import org.example.types.NoticeType;

import java.io.*;
import java.net.*;

@Setter
@Getter
@NoArgsConstructor
public class Client implements Runnable {
    private final int tcpClientPort = 9999;

    private final CommandProcessor commandProcessor = new CommandProcessor(this);
    private final Socket socket = new Socket();
    private final ClientPacketSender clientPacketSender = new ClientPacketSender(socket);
    private final ServerHandler serverHandler = new ServerHandler(this, clientPacketSender);
    private final ClientServiceGiver clientServiceGiver = new ClientServiceGiver(this, serverHandler);

    private Boolean isRegister = false;
    private String clientId;
    public void run() {
        try {
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", tcpClientPort));
            System.out.print("\n[ Success connecting ] \n");
            Thread thread = new Thread(serverHandler);
            thread.start();

        } catch (IOException e) {
            System.out.println("Server connection End");
        }
    }

    public void processCommand(String command) throws IOException {
        ProcessedObject processedObject = commandProcessor.extract(command, isRegister);
        if (processedObject == null) {
            System.out.println("\"Register First\\n /r or /R your ID\"\n type correct command!!\n");
            return;
        }
        // array:ost = [MessageTypeCode, messageType]
        if (processedObject.getMessageTypeCode() == MessageTypeCode.File_START_INFO) {
            System.out.println("File Start Info received!");
            serverHandler.setFileSender((FileStartInfo) processedObject.getMessageType());
            return;
        }
        if (processedObject.getMessageTypeCode() == MessageTypeCode.FIN) {
            closeSocket((NoticeType) processedObject.getMessageType());
            return;
        }
        serverHandler.sendPacket(processedObject.getMessageTypeCode(), processedObject.getMessageType());
    }

    public void service(Message message)  {
        clientServiceGiver.service(message, MessageProcessor.makeMessageType(message));
    }

    private void closeSocket(NoticeType finType) {
        serverHandler.sendPacket(MessageTypeCode.FIN,finType);
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}