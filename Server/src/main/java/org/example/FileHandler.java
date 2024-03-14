package org.example;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class FileHandler implements Runnable {
    private final Server server;
    private final Socket client;

    private DataOutputStream dataOutputStream;

    private final HashMap<String, HashMap<Integer, byte[]>> fileMap = new HashMap<>();


    public FileHandler(Server server, Socket socket) {
        this.server = server;
        this.client = socket;
        System.out.println("Run File_handler");
    }

    @Override
    public void run() {
        try {
            while (true) {
                OutputStream outputStream = client.getOutputStream();
                dataOutputStream = new DataOutputStream(outputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFile(String id) {
        // g
    }

    public void storeFile(byte[] body) {
        int recieverIdLength = ByteBuffer.wrap(body, 0, 4).getInt();
        String reciverId = new String(body, 4, recieverIdLength);
        int seq = ByteBuffer.wrap(body, 8, 4).getInt();
        byte[] fileByte = ByteBuffer.wrap(body, 12, body.length - recieverIdLength - 4 - 4).array();

        HashMap<Integer,byte[]> seqFileMap = new HashMap<>();
        fileMap.put(reciverId, seqFileMap);
    }

    private byte[] combineFile(String id) {
        ByteBuffer byteBuffer = allocateByteBufferFromMap(fileMap.get(id));
        for (int i = 0; i < fileMap.get(id).size(); i++) {
            byteBuffer.put(fileMap.get(id).get(i));
        }
        return byteBuffer.array();
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
