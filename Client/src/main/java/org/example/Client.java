package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client implements Runnable {
    private Boolean register = false;

    private MessageType messageType = MessageType.COMMENT;

    public void run(){
        Socket socket = new Socket();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try{
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", 9999));
            System.out.print("\n [ Success ... ]");

            while(true){
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                byte[] messageByte = null;
                String message = null;

                String command = bufferedReader.readLine();
                // command메서드로 나눌까?
                if (command.startsWith("/register")){
                    message = command.substring(10);
                    messageType = MessageType.REGISTER_ID;
                    messageByte = Share.getHeaderPacketByte(message.getBytes(), messageType);
                } else if(command.startsWith("/quit")){
                    message = command.substring(6);
                    messageType = MessageType.QUIT;
                    messageByte = Share.getHeaderPacketByte(message.getBytes(), messageType);
                } else{
                    messageType = MessageType.COMMENT;
                    messageByte = Share.getHeaderPacketByte(command.getBytes(), messageType);
                }

                System.out.println("\n[ Data Send Success ]\n" + message);
                sendPacket(messageByte, this.messageType, socket);

                // input -> type별로 액션 나누기
                int receiveLength = dataInputStream.readInt();
                if(receiveLength > 0){
                    // type별로 나누기
                    byte receiveByte[] = new byte[receiveLength];
                    dataInputStream.readFully(receiveByte, 0, receiveLength);
                    System.out.println("\n[ Data Receive Success ]\n" + message);
                } else if(receiveLength == 0){
                    break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void sendPacket(byte[] messageByte, MessageType messageType, Socket socket) throws IOException {
        try{
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            // type이 등록이 아니고, 등록 안된경우 보내지 못하도록
            if(messageType.ordinal() != 0 && !register){
                System.out.println("register first");
                return;
            }
            dataOutputStream.writeInt(messageByte.length);
            // 바이트 배열 전송
            dataOutputStream.write(messageByte, 0, messageByte.length);
            // 데이터 전송 후 스트림 닫기
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}