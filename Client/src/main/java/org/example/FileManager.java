package org.example;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileManager{
    private final Client client;
    private final HashMap<String, TreeMap<Integer, byte[]>> fileMap = new HashMap<>();

    private final String outputPath = "C:\\Users\\user\\Desktop\\BE metoring\\socket\\Client\\src\\main\\java\\org\\example";

    public FileManager(Client client) {
        this.client = client;
        System.out.println("Run File_handler");
    }

    public void storeFile(byte[] body) {
        if (fileMap.containsKey(FileProcessor.getReceiverId(body))) {
            return;
        }
        int receiverIdLength = FileProcessor.getReceiverIdLength(body);
        int seq = ByteBuffer.wrap(body, 8 + receiverIdLength, 4).getInt();
        byte[] fileByte = ByteBuffer.wrap(body, 12 + receiverIdLength, body.length - 4 - receiverIdLength - 4).array();

        TreeMap<Integer, byte[]> seqFileMap = new TreeMap<>();
        seqFileMap.put(seq, fileByte);
        fileMap.put(FileProcessor.getReceiverId(body), seqFileMap);
    }

    // fileMap = {FileName: {seq: byte[]}}
    public void testStore(byte[] body) {
        System.out.println("Client: filePacket received : " + Arrays.toString(body));
        // original body를 들고있다.
        TreeMap<Integer, byte[]> seqFileMap = new TreeMap<Integer, byte[]>();
        seqFileMap.put(FileProcessor.getFileSeq(body), FileProcessor.getFileByte(body));
        fileMap.put(FileProcessor.getFileName(body), seqFileMap);
    }

    public String getOutputPath() {
        return outputPath;
    }
    public byte[] getCombinedFile(String id) throws IllegalAccessException {
        TreeMap<Integer, byte[]> fileSeq = fileMap.get(id);
        if (fileSeq == null) {
            throw new IllegalAccessException("ID not found: " + id);
        }

        ByteBuffer combinedFile = ByteBuffer.allocate(fileSeq.values().stream().mapToInt(arr -> arr.length).sum());
        fileSeq.forEach((seq, bytes) -> combinedFile.put(bytes));
        return combinedFile.array();
    }

    public byte[] getCombineFileTestByte(byte[] fileNameByte) {
        String fullFileName = new String(fileNameByte, StandardCharsets.UTF_8);
        String fileName = fullFileName.split("\\.")[0];
        client.print("combined start: " + fileName);
        printFileMap();
        int totalLength = 0;
        for (int seq : fileMap.get(fileName).keySet()) {
            totalLength += fileMap.get(fileName).get(seq).length;
        }
        ByteBuffer totalBuffer = ByteBuffer.allocate(totalLength);
        for (int seq : fileMap.get(fileName).keySet()) {
            totalBuffer.put(fileMap.get(fileName).get(seq));
        }
        return totalBuffer.array();
    }

    private void printFileMap() {
        Set<String> keySet = fileMap.keySet();
        keySet.forEach(key -> {
            client.print("key: " + key + ", value: " + fileMap.get(key));
        });
    }

    private ByteBuffer allocateByteBufferFromMap(HashMap<Integer, byte[]> seqFileMap) {
        int totalSize = seqFileMap.values().stream().mapToInt(arr -> arr.length).sum();
        return  ByteBuffer.allocate(totalSize);
    }
    //
    // 데이터 저장 - seq, data로
    // 저장된 데이터 순차적으로 합치기
    // 합친 데이터 전송

}
