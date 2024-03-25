package org.example;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileManager{
    private final Client client;
    private final HashMap<String, TreeMap<Integer, byte[]>> fileMap = new HashMap<>();

    public FileManager(Client client) {
        this.client = client;
        System.out.println("Run File_handler");
    }


    // fileMap = {FileName: {seq: byte[]}}
    public void store(byte[] body) {
        System.out.println("\nin test Store: Client: filePacket received : " + Arrays.toString(body));
        // original body를 들고있다.
        ByteBuffer bodyBuffer = ByteBuffer.wrap(body);

        int fileNameLengthSize = 4;
        int fileNameLength= bodyBuffer.getInt();
        byte[] fileNameByte = new byte[fileNameLength];

        String fileName = new String(fileNameByte);
        System.out.println("store fileName: " + fileName );
        System.out.println("remain bodyBuffer" + Arrays.toString(bodyBuffer.array()) + "\nposition: " + bodyBuffer.position());

        int seqSize = 4;
        int seq = bodyBuffer.getInt();
        System.out.println("store seq: " + seq);
        byte[] fileByte = new byte[body.length - fileNameLengthSize - fileNameLength - seqSize];
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
    }

    public byte[] getCombineFile(byte[] fileNameByte) {
        String fullFileName = new String(fileNameByte, StandardCharsets.UTF_8);
        String fileName = fullFileName.split("\\.")[0];

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
}