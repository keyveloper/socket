package org.example;

import javax.swing.text.Style;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Client implements Runnable {
    private final int tcpClientPort = Share.portNum;
    private Boolean registered = false;
    private DataOutputStream dataOutputStream;

    private FileManager fileManager;

    Socket socket;

    public Client() {
        socket = new Socket();
        fileManager = new FileManager();
    }
    public void run() {
        long threadId = Thread.currentThread().getId();
        System.out.println("myid: "+  threadId);
        try {
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", tcpClientPort));
            System.out.print("\n [ Success connecting ] \n");

            while (true) {
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                int inAllLength = dataInputStream.readInt();
                if(inAllLength > 0) {
                    byte[] inLengthByte = new byte[4];
                    dataInputStream.readFully(inLengthByte);
                    // int messageLength = Share.readInputLength(inLengthByte);

                    byte[] inTypeByte = new byte[4];
                    dataInputStream.readFully(inTypeByte);
                    int typeInt = ByteBuffer.wrap(inTypeByte).getInt();
                    MessageType inMessageType = MessageType.values()[typeInt];


                    byte[] inMessageByte = new byte[inAllLength - 8];
                    dataInputStream.readFully(inMessageByte);
                    String message = Share.readInputMessage(inMessageByte);

                    if (inMessageType == MessageType.FIN_ACK) {
                        actionByType(inMessageType, "");
                        break;
                    }

                    if (inMessageType == MessageType.FILE || inMessageType == MessageType.FILE_END) {
                        actionByFile(inMessageType, inMessageByte);
                    }

                    actionByType(inMessageType, message);
                } else if(inAllLength == 0){
                    // quit
                    break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void processCommand(String command){
        if (command.startsWith("/R") || command.startsWith("/r")) {
            processRegister(command);
        }

        if (command.startsWith("/F") || command.startsWith("/f")) {
            processFin(command);
        }

        if (command.startsWith("/N") || command.startsWith("/n")) {
            processChangeId(command);
        }

        if (command.startsWith("/W") || command.startsWith("/w")) {
            processWhisper(command);
        }

        processComment(command);

    }

    private void processRegister(String command) {
        byte[] packet = HeaderMaker.makeHeader(MessageType.REGISTER_ID, command.substring(3).getBytes(););
        sendPacket(packet);
    }

    private void processFin(String command) {
        byte[] packet = HeaderMaker.makeHeader(MessageType.FIN, command.substring(3).getBytes());
        sendPacket(packet);
    }

    private void processChangeId(String command) {
        String[] parts = command.substring(3).split(" ", 2);
        ByteBuffer partsBuffer = ByteBuffer.allocate(parts[0].length() + parts[1].length());
        partsBuffer.put(parts[0].getBytes());
        partsBuffer.put(parts[1].getBytes());
        byte[] packet = HeaderMaker.makeHeader(MessageType.CHANGE_ID, partsBuffer.array());
        sendPacket(packet);
    }

    private void processWhisper(String command) {
        String[] parts = command.substring(3).split(" ", 2);
        ByteBuffer partsBuffer = ByteBuffer.allocate(parts[0].length() + parts[1].length());
        partsBuffer.put(parts[0].getBytes());
        partsBuffer.put(parts[1].getBytes());
        byte[] packet = HeaderMaker.makeHeader(MessageType.CHANGE_ID, partsBuffer.array());
        sendPacket(packet);
    }

    private void processComment(String command) {
        byte[] packet = HeaderMaker.makeHeader(MessageType.COMMENT, command.getBytes());
    }


    private void actionByType(MessageType inputType, String message) throws IOException {
        switch (inputType) {
            case COMMENT, WHISPER, NOTICE:
                System.out.println("\n" + message + "\n");
                break;
            case ALREADY_EXIST_ID:
                System.out.println("This ID already Exist");
                break;
            case REGISTER_SUCCESS:
                System.out.println("Register Success!!");
                this.registered = true;
                break;
            case FIN_ACK:
                System.out.println("Connetion close!");
                dataOutputStream.close();
                socket.close();
                break;
        }
    }

    private void actionByFile(MessageType messageType, byte[] body) throws IllegalAccessException, IOException {
        switch (messageType) {
            case FILE:
                fileManager.storeFile(body);
                break;
            case FILE_END:
                byte[] combinedFileByte = fileManager.getCombinedFile(FileProcessor.getReceiverId(body));
                FileOutputStream fileOutputStream = new FileOutputStream(fileManager.getOutputPath());
                fileOutputStream.write(body);
                System.out.println("File has been saved successfully to: " + fileManager.getOutputPath());
                break;
        }
    }



    private void sendPacket(byte[] packet) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.write(packet, 0, packet.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFile(String id, String filePath) {
        File file = new File(filePath);
        // 1MB단위로 읽어주기
        // file - 100mb
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] oneRead = new byte[1024 * 1024]; // 1mb반위로 읽기 1024 * 1024
            int bytesRead;
            int seq = 0;
            // 파일 데이터를 모두 읽을 때 까지 -> 연속적으로 계속 전송
            while ((bytesRead = fileInputStream.read(oneRead)) != -1) {
                // 파일 패킷추가
                dataOutputStream.write(FileProcessor.getFileHeaderByCommand(MessageType.FILE, id, seq, oneRead), 0, bytesRead);
                dataOutputStream.flush();
                seq ++;
            }

            System.out.println("file 전송 완료");
            dataOutputStream.write(Share.getPacketHeader(MessageType.FILE_END, id));
            dataOutputStream.flush();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }}

}