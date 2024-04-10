package org.example;

import lombok.Data;
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

    public void sendFile() {
        String receiverId = fileStartType.getReceiver();
        String fileName = fileStartType.getFileName();
        String filePath = fileStartType.getFilePath();

        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            byte[] fileBuffer = new byte[1024 * 1024]; // 1MB read per once
            int bytesRead;
            int seq = 0;
            while ((bytesRead = fileInputStream.read(fileBuffer)) != -1) {
                byte[] actualRead = Arrays.copyOf(fileBuffer, bytesRead);

                FileType fileType = new FileType(false, receiverId, fileName, seq, actualRead);
                seq += 1;
                byte[] packet = PacketMaker.makePacket(MessageTypeCode.FILE, fileType);
                clientPacketSender.sendPacket(packet);
            }
            // end -> true
            FileType endFileType = new FileType(true, receiverId, fileName, -1, null);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
