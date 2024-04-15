package org.example;

import lombok.Data;
import org.example.types.FileStartType;
import org.example.types.MessageType;
import org.example.types.MessageTypeCode;

import java.util.HashMap;



@Data
public class ServerHandler implements Runnable{
    private final Client client;
    private final ClientPacketSender clientPacketSender;
    private final HashMap<String, FileSender> fileSenderHashMap = new HashMap<>();
    private boolean isSendingFile;

    @Override
    public void run() {
        ClientPacketReader clientPacketReader = new ClientPacketReader(client.getSocket());
        while (true) {
            Message message = clientPacketReader.readPacket();
            if (message == null) {
                break;
            }
            client.service(message);
        }
    }

    public void sendPacket(MessageTypeCode messageTypeCode, MessageType messageType) {
        byte[] packet = PacketMaker.makePacket(messageTypeCode, messageType);
        clientPacketSender.sendPacket(packet);
    }

    public void sendFileStart(FileStartType fileStartType) {
        FileSender fileSender = setFileSender(fileStartType);
        clientPacketSender.sendPacket(PacketMaker.makePacket(MessageTypeCode.FILE_START, fileStartType));
        fileSender.sendFile();

    }

    private FileSender setFileSender(FileStartType fileStartType) {
        isSendingFile = true;
        FileSender fileSender = new FileSender(fileStartType.getFileName(), fileStartType.getFilePath(), this);
        fileSender.setReceiver(fileStartType.getId());
        fileSenderHashMap.put(fileStartType.getId(), fileSender);
        return fileSender;
    }

    // 기존 아이디 받아와야해
    public void informReceiverChange(String oldId, String newId) {
        FileSender fileSender = fileSenderHashMap.get(oldId);
        fileSender.changReceiver(newId);
        fileSenderHashMap.remove(oldId);
        fileSenderHashMap.put(newId, fileSender);
    }

    public boolean checkFileSending() {
        return isSendingFile;
    }


}
