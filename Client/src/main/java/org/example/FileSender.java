package org.example;

import lombok.Data;
import org.example.types.FileEndType;
import org.example.types.FileType;
import org.example.types.MessageTypeCode;

import javax.swing.plaf.synth.SynthUI;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

@Data
public class FileSender {
    private final String fileName;
    private final String filePath;
    private final String sender;
    private String receiver;
    private final ServerHandler serverHandler;

    public void sendFile() {

        try {
            System.out.println("File read start {\n");
            FileInputStream fileInputStream = new FileInputStream(filePath);
            byte[] fileBuffer = new byte[1024 * 1024]; // 1MB read per once
            int bytesRead;
            int seq = 0;
            while ((bytesRead = fileInputStream.read(fileBuffer)) != -1) {
                byte[] actualRead = Arrays.copyOf(fileBuffer, bytesRead);

                FileType fileType = new FileType(sender, receiver, fileName, seq, actualRead);
                seq += 1;
                serverHandler.sendPacket(MessageTypeCode.FILE, fileType);
                System.out.println("seq: " + seq + "file was sent!!\n fileLength: " + actualRead.length);
            }
            sendEnd();
            fileInputStream.close();
            // end -> true
        } catch (FileNotFoundException e) {
            System.out.println("Can not find file");
        } catch (IOException e) {
            System.out.println("fileInputStream error");
        }
    }

    private void sendEnd() {
        System.out.println("} \nFile read End ");
        serverHandler.sendPacket(MessageTypeCode.FILE_END, new FileEndType(receiver, fileName));
        serverHandler.removeFileSender(receiver);
    }
    public void changReceiver(String receiver) {
        this.receiver = receiver;
    }

}
