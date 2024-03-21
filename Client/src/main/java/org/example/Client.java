package org.example;

import javax.swing.text.Style;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.*;

public class Client implements Runnable {
    private final int tcpClientPort = Share.portNum;
    private Boolean registered = false;
    private DataOutputStream dataOutputStream;

    private FileManager fileManager;

    Socket socket;

    public Client() {
        socket = new Socket();
        fileManager = new FileManager(this);
    }
    public void run() {
        long threadId = Thread.currentThread().getId();
        System.out.println("myid: "+  threadId);
        try {
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", tcpClientPort));
            System.out.print("\n [ Success connecting ] \n");

            while (true) {
                OutputStream outputStream = socket.getOutputStream();
                dataOutputStream = new DataOutputStream(outputStream);
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

                    if (inMessageType == MessageType.TEST) {;
                        storeFile(inMessageByte);
                    }

                    if (inMessageType == MessageType.TEST_SEND_END) {;
                        combineFile(inMessageByte);
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

    // store in structure
    private void storeFile(byte[] body) {
        fileManager.testStore(body);
    }

    // save in path
    private void saveFile(byte[] body) {
        // has only body(flieName, seq, fileByte[])_
        System.out.print("start to save file/ \n data: " + Arrays.toString(body));
        try {
            String randomFileName = UUID.randomUUID().toString() + ".txt";
            String directoryPath = "C:\\Users\\user\\Desktop\\BEmetoring\\file_test\\output";
            File file = new File(directoryPath, randomFileName);
            FileOutputStream fileOutputStream =  new FileOutputStream(file);
            fileOutputStream.write(body);
            fileOutputStream.flush();
            System.out.println("File saved successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void combineFile(byte[] fileNameByte) {
        byte[] totalFileByte = fileManager.getCombineFileTestByte(fileNameByte);
        saveFile(totalFileByte);
    }

    public void processCommand(String command){
        try {
            MessageType messageType = getMessageTypeByCommand(command);

            if (messageType == null) {
                System.out.println("Invalid command. Please try again.");
                return;
            }
            System.out.println("command message type: " + getMessageTypeByCommand(command));
            switch (messageType) {
                case REGISTER_ID:
                    if (registered) {
                        System.out.println("you Already Register ID");
                        break;
                    }
                    sendPacket(MessageType.REGISTER_ID, getBodyMessage(command));
                    System.out.println("send Id:" + getBodyMessage(command));
                    break;
                case FIN:
                    System.out.println("sending FIN Packet");
                    sendPacket(MessageType.FIN, "");
                    break;
                case FILE:
                    String[] parts = getBodyMessage(command).split(" ", 3);
                    if (parts.length < 3) {
                        System.out.println("wrong command \n command: /F id filepath or filename");
                    }
                    // parts[] = [/f id path]
                    sendFile(parts[1], "C:\\Users\\user\\Desktop\\BE metoring\\socket\\Client\\src\\main\\java\\org\\example\\hi.txt");
                    break;
                case TEST:
                    sendTestFile(getBodyMessage(command));
                    break;
                default:
                    if (registered) {
                        System.out.println("sending default mode");
                        sendPacket(getMessageTypeByCommand(command), getBodyMessage(command));
                    } else {
                        System.out.println("Register first! \n command : /R");
                    }
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void print(String message) {
        System.out.println(message);
    }

    private void sendTestFile(String fileName) {
        File file = new File("C:\\Users\\user\\Desktop\\BEmetoring\\file_test", fileName);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] readByte = new byte[10];
            int byteRead;
            int seq = 0;
            while ((byteRead = fileInputStream.read(readByte)) != -1) {
                System.out.println("readByte: " + Arrays.toString(readByte));
                System.out.println("byteRead: " + byteRead);
                byte[] actualBytessRead = Arrays.copyOf(readByte, byteRead);

                byte[] testHeader = FileProcessor.getTestFileHeader(MessageType.TEST, fileName, seq, actualBytessRead);
                System.out.println("made packet: " + Arrays.toString(testHeader));
                System.out.print("\n seq: " + seq + " sending file packet\n");
                System.out.println("testHeader length: " + testHeader.length);
                dataOutputStream.write(testHeader,0, testHeader.length);
                dataOutputStream.flush();
                seq ++;

            }
            System.out.println("all filePacket be sent");
            sendPacket(MessageType.TEST_SEND_END, fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void sendPacket(MessageType type, String body) {
        try {
            System.out.println("add headerm type, body" + type + body);
            byte[] sendingByte = Share.getPacketHeader(type, body);
            System.out.println(Arrays.toString(sendingByte) + sendingByte.length);
            dataOutputStream.write(sendingByte, 0, sendingByte.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFile(String id, String filePath) {
        File file = new File(filePath);
        // 1MB단위로 읽어주기
        // file - 100mb
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] oneRead = new byte[1024 * 1024]; // 1mb반위로 읽기 1024 * 1024
            int bytesRead;
            int seq = 0;
            // 파일 데이터를 모두 읽을 때 까지 -> 연속적으로 계속 전송
            while ((bytesRead = fileInputStream.read(oneRead)) != -1) {
                System.out.println(bytesRead);
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

    private MessageType getMessageTypeByCommand(String command){
        if (command.startsWith("/R")) {
            return MessageType.REGISTER_ID;
        } else if (command.startsWith("/Q")) {
            return MessageType.FIN;
        } else if (command.startsWith("/N")) {
            return MessageType.CHANGE_ID;
        } else if (command.startsWith("/W")) {
            return MessageType.WHISPER;
        } else if (command.startsWith("/F")) {
            return MessageType.FILE;
        } else if (command.startsWith("/T")) {
            return MessageType.TEST;
        }
          else if (command.startsWith("/")){
            return null;
        }
        return MessageType.COMMENT;
    }

    private String getBodyMessage(String command){
        String bodyMessage;
        if (command.startsWith("/Q")){
            return "";
        } else if (command.startsWith("/")) {
            return command.substring(3);
        }
        bodyMessage = command;
        return bodyMessage;
    }


}