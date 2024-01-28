package org.example;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import org.example.MessageType;
import org.example.Share;


public class Client implements Runnable{

    Socket socket;


    public void run(){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        socket = new Socket();
        System.out.println("\n [Request...] \n");

        try{
            socket.connect(new InetSocketAddress("localhost", 9999));
            System.out.println(" \n [ Success ] \n");

            while(true) {
                // 서버 대이터 출력(지속)
                String receiveMessage = null;
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                int receiveLength = dataInputStream.readInt();

                if(receiveLength > 0){
                    byte[] receiveByte = new byte[receiveLength];
                    dataInputStream.readFully(receiveByte, 0, receiveLength);
                    receiveMessage = new String(receiveByte);
                    System.out.println(" \n [Data Receive Success ]\n" + receiveMessage);
                }

                // 임시로 종료
                if(receiveLength == 0){
                    inputStream.close();
                    socket.close();
                    break;
                }

                // 서버로 데이터 전송(지속)
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                String message = bufferedReader.readLine();
                String messageType = bufferedReader.readLine();
                byte[] idBytes = message.getBytes();
                byte[] finalIdBytes = Share.getHeader(idBytes, messageType);

                dataOutputStream.writeInt(finalIdBytes.length);
                dataOutputStream.write(finalIdBytes, 0, finalIdBytes.length);
                dataOutputStream.flush();

                System.out.println(" \n [Data send Success] \n" + message);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
