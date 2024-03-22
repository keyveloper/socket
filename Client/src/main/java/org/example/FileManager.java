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
    public void testStore(byte[] body, int fileLength) {
        System.out.println("in test Store: Client: filePacket received : " + Arrays.toString(body));
        // original body를 들고있다.
        ByteBuffer bodyBuffer = ByteBuffer.wrap(body);
        System.out.println("first bodyBuffer: " + Arrays.toString(bodyBuffer.array()));
        byte[] fileNameByte = new byte[4];
        bodyBuffer.get(fileNameByte);
        String fileName = new String(fileNameByte);
        System.out.println("store fileName: " + fileName );
        System.out.println("remain bodyBuffer" + Arrays.toString(bodyBuffer.array()) + "\nposition: " + bodyBuffer.position());

        int seq = bodyBuffer.getInt();
        System.out.println("store seq: " + seq);
        byte[] fileByte = new byte[fileLength];
        bodyBuffer.get(fileByte);
        System.out.println("store fileByte: " + Arrays.toString(fileByte));

        TreeMap<Integer, byte[]> seqFileMap;
        if (!fileMap.containsKey(fileName)) {
            seqFileMap = new TreeMap<Integer, byte[]>();
        } else {
            seqFileMap = fileMap.get(fileName);
        }
        seqFileMap.put(seq, fileByte);
        fileMap.put(fileName, seqFileMap);

        for (Map.Entry<Integer, byte[]> entry : fileMap.get(fileName).entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + Arrays.toString(entry.getValue()));
        }
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
        client.print("combined start: " + fileName + "\n");

        TreeMap<Integer, byte[]> seqFileMap = fileMap.get(fileName);
        int totalSize = 0;
        for (Map.Entry<Integer, byte[]> entry : seqFileMap.entrySet()) {
            totalSize += entry.getValue().length;
        }
        System.out.println("totalSize: " + totalSize);
        ByteBuffer totalBuffer = ByteBuffer.allocate(totalSize);


        for (byte[] fileByte : seqFileMap.values()) {
            totalBuffer.put(fileByte);
        }
        return totalBuffer.array();
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
