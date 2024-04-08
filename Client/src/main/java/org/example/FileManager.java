package org.example;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

@Data
public class FileManager {
    private final Client client;
    private final HashMap<String, TreeMap<Integer, byte[]>> fileMap = new HashMap<>();

    public void storeFilePiece(String fileName, int seq, byte[] fileByte) {
        TreeMap<Integer, byte[]> seqFileMap;
        if (fileMap.containsKey(fileName)) {
            seqFileMap = fileMap.get(fileName);
        } else {
            seqFileMap = new TreeMap<>();

        }
        seqFileMap.put(seq, fileByte);
        fileMap.put(fileName, seqFileMap);
        System.out.println("save file!! \nseq: " + seq);
    }

    public void saveFile(String fileName) throws IllegalAccessException {
        String savePath = "C:\\Users\\user\\Desktop\\BEmetoring\\file_test\\output";
        byte[] totalFileByte = combineFile(fileName);

        try {
            String randomFileName = UUID.randomUUID() + ".txt";
            File file = new File(savePath, randomFileName);
            FileOutputStream fileOutputStream =  new FileOutputStream(file);
            fileOutputStream.write(totalFileByte);
            fileOutputStream.flush();
            System.out.println("File saved successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private byte[] combineFile(String fileName) throws IllegalAccessException {
        TreeMap<Integer, byte[]> seqFileMap = fileMap.get(fileName);
        if (seqFileMap == null) {
            throw new IllegalAccessException("fileName not Found: " + fileName);
        }

        ByteBuffer combineBuffer = ByteBuffer.allocate(seqFileMap.values().stream().mapToInt(arr -> arr.length).sum());
        seqFileMap.forEach((seq, bytes) -> combineBuffer.put(bytes));
        return combineBuffer.array();
    }


}
