package org.example;

import lombok.Data;
import org.example.types.FileType;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.UUID;

@Data
public class FileSaver {
    private final String sender;
    private final String directory = "C:\\Users\\user\\Desktop\\BEmetoring\\test\\output\\";
    private RandomAccessFile file;

    private void set(){
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
        return directory + UUID.randomUUID();
    }

    public void save(int seq, byte[] fileBytes) {
        long FILE_SEGMENT_SIZE = 1024 * 1024;
        // seq, fileByte[]
        if (this.file == null) {
            System.out.println("set empty File!!");
            set();
        }

        try {
            file.seek(FILE_SEGMENT_SIZE * seq);
            file.write(fileBytes);
        } catch (IOException e) {
            System.out.println("can't write file");
        }
    }

    public void writeSender() {
        // write Sender in End
        try {
            file.seek(file.length());
            String writer = " from " + sender;
            file.write(writer.getBytes());
            file.close();
        } catch (IOException e) {
            System.out.println("can't write writer");
        }
    }
}
