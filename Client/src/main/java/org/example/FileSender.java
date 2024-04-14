package org.example;

import lombok.Data;
import org.example.types.FileEndType;
import org.example.types.FileStartType;
import org.example.types.FileType;
import org.example.types.MessageTypeCode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

@Data
public class FileSender {
    private final FileStartType fileStartType;
    private final ClientPacketSender clientPacketSender;

    public void sendStart() {
        clientPacketSender.sendPacket(PacketMaker.makePacket(MessageTypeCode.FILE_START, fileStartType));
    }

    public void sendFile() {

        System.out.println("start to send file");
        String receiverId = fileStartType.getId();
        String fileName = fileStartType.getFileName();
        String filePath = fileStartType.getFilePath();
        System.out.println("FILE INFO: {\nreceiverID: " + receiverId + "\nfileName: " + fileName + "\nfilePath: " +filePath +"\n}");

        try {
            System.out.println("File read start {\n");
            FileInputStream fileInputStream = new FileInputStream(filePath);
            byte[] fileBuffer = new byte[1024 * 1024]; // 1MB read per once
            int bytesRead;
            int seq = 0;
            while ((bytesRead = fileInputStream.read(fileBuffer)) != -1) {
                byte[] actualRead = Arrays.copyOf(fileBuffer, bytesRead);
                System.out.println("File read: " + Arrays.toString(actualRead));

                FileType fileType = new FileType(false, receiverId, fileName, seq, actualRead);
                seq += 1;
                byte[] packet = PacketMaker.makePacket(MessageTypeCode.FILE, fileType);
                clientPacketSender.sendPacket(packet);
                System.out.println("seq: " + seq + "file was sent!!");
            }
            System.out.println("} \nFile read End ");
            // end -> true
            clientPacketSender.sendPacket(PacketMaker.makePacket(MessageTypeCode.FILE_END, new FileEndType(receiverId, fileName)));
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can not find file");
        } catch (IOException e) {
            System.out.println("fileInputStream error");
        }
    }
}
