package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.example.types.FileStartInfo;
import org.example.types.MessageType;
import org.example.types.MessageTypeCode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;


@Data
public class ServerHandler implements Runnable{
    private final Client client;
    private final ClientPacketSender clientPacketSender;
    private final HashMap<UUID, FileSender> fileSenderHashMap = new HashMap<>();
    @Override
    public void run() {
        ClientPacketReader clientPacketReader = new ClientPacketReader(client.getSocket());
        while (true) {
            Message message = clientPacketReader.readPacket();
            if (message == null) {
                break;
            }
            try {
                client.service(message);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendPacket(MessageTypeCode messageTypeCode, MessageType messageType) {
        byte[] packet = PacketMaker.makePacket(messageTypeCode, messageType);
        clientPacketSender.sendPacket(packet);
    }
    public void setFileSender(FileStartInfo fileStartInfo) {
        // FileSenderMap : {"fileId" : "FileSender"}
        System.out.println("set fileSaver!");
        fileSenderHashMap.put(fileStartInfo.getFileId(), new FileSender(client.getClientId(), fileStartInfo.getFileId(), fileStartInfo.getFilePath(), this));
        sendPacket(MessageTypeCode.File_START_INFO, fileStartInfo);
    }
    public void removeFileSender(UUID fileId) {
        System.out.println("[in serverHandler]\nfileId: " + fileId + "sender was removed!!\n");
        fileSenderHashMap.remove(fileId);
    }
    // get old Id Receiver Sender
}
