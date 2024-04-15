package org.example;

import lombok.Data;
import org.example.types.FileType;
import org.example.types.MessageType;
import org.example.types.MessageTypeCode;

import java.util.HashMap;



@Data
public class ServerHandler implements Runnable{
    private final Client client;
    private final ClientPacketSender clientPacketSender;
    private final HashMap<String, FileSender> fileSenderHashMap = new HashMap<>();

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
    public void sendFileStart(FileType fileType) {
        // FileSenderHashMap = <Receiver, FileSender>
        FileSender fileSender;
        if (fileSenderHashMap.containsKey(fileType.getReceiver())) {
            fileSender = fileSenderHashMap.get(fileType.getReceiver());
        } else {
            fileSender = new FileSender(fileType.getFileName(), fileType.getFilePath(), fileType.getSender(), this);
            fileSender.setReceiver(fileType.getReceiver());
            fileSenderHashMap.put(fileSender.getReceiver(), fileSender);
        }
        fileSender.sendFile();
    }

    public void removeFileSender(String receiver) {
        fileSenderHashMap.remove(receiver);
    }


    // get old Id Receiver Sender
    public void informReceiverChange(String oldId, String newId) {
        FileSender fileSender = fileSenderHashMap.get(oldId);
        fileSender.changReceiver(newId);
        fileSenderHashMap.remove(oldId);
        fileSenderHashMap.put(newId, fileSender);
    }

    public boolean checkFileSender(String id) {
        return fileSenderHashMap.containsKey(id);
    }


}
