package org.example;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;

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

    public void sendFile(MessageType messageType, byte[] body) {
        while (messageType == MessageType.FILE_END) {
            // id길이 출력 = 4바이트
            int idLength = ByteBuffer.wrap(body, 0, 4).getInt();
            // id길이만큼 읽어서 id 뽑아내기
            String id = new String(body, 4, idLength);

        }



    }

    private void storeFile(String id, byte[] bytes) {
        fileMap.put(id, )
    }

    // 데이터 저장 - seq, data로
    // 저장된 데이터 순차적으로 합치기
    // 합친 데이터 전송

}
