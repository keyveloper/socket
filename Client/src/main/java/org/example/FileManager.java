package org.example;

import lombok.Data;
import org.example.types.FileType;

import java.io.*;
import java.nio.ByteBuffer;

@Data
public class FileManager {
    private final String fileName;
    private final String sender;
    private final String directory = "C:\\Users\\yangd\\Desktop\\BE\\test\\output\\";
    private RandomAccessFile file;

    public void set(){
        String filePath = generateUniqueFileName();
        // fileName에 해당하는 빈 파일 set하기
        try {
            file = new RandomAccessFile(filePath, "rw" );
            System.out.println("empty file maded: " +filePath);
        } catch (IOException e) {
            System.out.println("can't set new file");
        }
    }

    private String generateUniqueFileName() {
        String fullPath = directory + File.separator + fileName;
        File file = new File(fullPath);
        int count = 1;
        while (file.exists()) {
            fullPath = directory + File.separator + fileName + "_" + count;
            file = new File(fullPath);
            count++;
        }
        return fullPath;
    }

    public void save(FileType fileType) {
        long FILE_SEGMENT_SIZE = 1024 * 1024;
        // seq, fileByte[]
        if (this.file == null) {
            throw new FileNotSetException("file wasn't set");
        }

        try {
            file.seek(FILE_SEGMENT_SIZE * fileType.getSeq());
            file.write(fileType.getFileByte());
        } catch (IOException e) {
            System.out.println("can't write file" + fileType);
        }
    }

    public void writeSender() {
        // write Sender in End
        try {
            int SPACE_BAR_SIZE = 1;
            file.seek(file.length());

            ByteBuffer senderBuffer = ByteBuffer.allocate(SPACE_BAR_SIZE + sender.length());
            senderBuffer.put(" from ".getBytes());
            senderBuffer.put(sender.getBytes());
            file.write(senderBuffer.array());
            file.close();
        } catch (IOException e) {
            System.out.println("can't write writer");
        }
    }
}
