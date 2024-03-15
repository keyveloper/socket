package org.example;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class FileManager{
    private final Server server;
    private final Socket client;
    private final HashMap<String, TreeMap<Integer, byte[]>> fileMap = new HashMap<>();

    public String getReceiverId(byte[] body) {
        int receiverIdLength = ByteBuffer.wrap(body, 0, 4).getInt();
        String receiverId = new String(body, 4, receiverIdLength);
        return receiverId;
    }

    public FileManager(Server server, Socket socket, DataOutputStream dataOutputStream) {
        this.server = server;
        this.client = socket;
        System.out.println("Run File_handler");
    }

    public void storeFile(byte[] body) {
        int receiverIdLength = ByteBuffer.wrap(body, 0, 4).getInt();
        String receiverId = new String(body, 4, receiverIdLength);
        if (fileMap.containsKey(receiverId)) {
            // one account one file
            return;
     g   }
        int seq = ByteBuffer.wrap(body, 8 + receiverIdLength, 4).getInt();
        byte[] fileByte = ByteBuffer.wrap(body, 12 + receiverIdLength, body.length - 4 - receiverIdLength - 4).array();

        TreeMap<Integer, byte[]> seqFileMap = new TreeMap<>();
        fileMap.put(receiverId, seqFileMap);
    }

    private byte[] getCombineFile(String id) throws IllegalAccessException {
        TreeMap<Integer, byte[]> fileSeq = fileMap.get(id);
        if (fileSeq == null) {
            throw new IllegalAccessException("ID not found: " + id);
        }

        ByteBuffer combinedFile = ByteBuffer.allocate(fileSeq.values().stream().mapToInt(arr -> arr.length).sum());
        fileSeq.forEach((seq, bytes) -> combinedFile.put(bytes));
        return combinedFile.array();
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
