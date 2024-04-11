package org.example;

import lombok.Data;
import org.example.types.FileType;

import java.io.*;

@Data
public class FileManager {
    private final String fileName;
    private final String writePath = "C:\\Users\\yangd\\Desktop\\BE\\test\\output";
    private RandomAccessFile file;

    public void set(){
        // fileName에 해당하는 빈 파일 set하기
        try {
            RandomAccessFile file = new RandomAccessFile(writePath + fileName, "rw" );
            file.close();

            System.out.println("empty file maded: " + writePath + fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            throw new RuntimeException(e);
        }


    }
}
