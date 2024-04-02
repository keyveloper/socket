package org.example;

import lombok.Data;

import javax.swing.text.AttributeSet;
import javax.swing.text.FieldView;
import javax.swing.text.Style;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class Client implements Runnable {
    private final int tcpClientPort = Share.portNum;
    private Boolean registered = false;
    private final FileManager fileManager;

    Socket socket;

    public Client() {
        socket = new Socket();
        fileManager = new FileManager(this);
    }
    public void run() {
        try {
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", tcpClientPort));
            System.out.print("\n[ Success connecting ] \n");
            ClientPacketSender clientPacketSender = new ClientPacketSender(socket);
            ClientPacketReader clientPacketReader = new ClientPacketReader(socket);

            while (true) {
                Message receivedMessage = clientPacketReader.readPacket();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processCommand(String command) {
        CommandSeparator commandSeparator = new CommandSeparator(command);
        MessageType messageType = commandSeparator.getMessageType();
        HashMap<String, Object> contentMap = commandSeparator.getContentMap();

        makePacket(messageType, contentMap);
    }

    private void makePacket(MessageType messageType, HashMap<String, Object> contentMap) {

    }
    private void processMessage(Message message) {
        System.out.println("start procee message: " + message.getMessageType());
        switch (message.getMessageType()) {
            case COMMENT, WHISPER, NOTICE:
                System.out.println("\n" + new String(message.getBody()) + "\n");
                break;
            case ALREADY_EXIST_ID:
                System.out.println("This ID already Exist");
                break;
            case REGISTER_SUCCESS:
                System.out.println("Register Success!!");
                this.registered = true;
                break;
            case FILE:
                storeFile(message.getBody());
                break;
            case FILE_END:
                combineFile(message.getBody());
                break;
            case FIN_ACK:
                System.out.println("Connetion close!");
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream()) ;
                    dataOutputStream.close();
                    dataInputStream.close();
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }

    private void processFile(String command) {
        // /f id fileName
        String[] parts = command.substring(3).split(" ", 2);
        System.out.println(Arrays.toString(parts));
        sendFile(parts[0].getBytes(), parts[1].getBytes());
    }

    private void sendFile(byte[] receiver, byte[] fileName) {
        // home pc String filePath = "C:\\Users\\user\\Desktop\\BEmetoring\\file_test";
        String filePath = "C:\\Users\\yangd\\OneDrive\\바탕 화면\\filetest";
        File file = new File(filePath, new String(fileName));
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] readByte = new byte[10];
            String onlyName = new String(fileName).split("\\.")[0];
            int receiverLengthSize = Integer.BYTES;
            int fileNameSize = Integer.BYTES;
            int seqSize = Integer.BYTES;
            int byteRead;
            int seq = 0;
            while ((byteRead = fileInputStream.read(readByte)) != -1) {
                byte[] actualRead = Arrays.copyOf(readByte, byteRead);
                // idlength(4) + id + fileNameLength(4) + fileName + seq + filebyte
                ByteBuffer bodyBuffer = ByteBuffer.allocate(receiverLengthSize + receiver.length + fileNameSize + onlyName.getBytes().length + seqSize + actualRead.length);
                System.out.println("bodybuffer size: " + bodyBuffer.capacity());
                bodyBuffer.putInt(receiver.length);
                bodyBuffer.put(receiver);
                bodyBuffer.putInt(onlyName.getBytes().length);
                bodyBuffer.put(onlyName.getBytes());
                bodyBuffer.putInt(seq);
                bodyBuffer.put(actualRead);
                System.out.println("bodyBuffer: (not head)" + Arrays.toString(bodyBuffer.array())); // 여기서 문제 -> 할당하는 부분 다시

                byte[] packet = HeaderAdder.addHeader(MessageType.FILE, bodyBuffer.array());
                System.out.println("made packet: " + Arrays.toString(packet));
                dataOutputStream.write(packet,0, packet.length);
                dataOutputStream.flush();
                seq ++;
            }
             System.out.println("all filePacket be sent");
            ByteBuffer endBuffer = ByteBuffer.allocate(receiverLengthSize + receiver.length + fileNameSize + onlyName.getBytes().length);
            endBuffer.putInt(receiver.length);
            endBuffer.put(receiver);
            endBuffer.putInt(onlyName.getBytes().length);
            endBuffer.put(onlyName.getBytes());
            byte[] endPacket = HeaderAdder.addHeader(MessageType.FILE_END, endBuffer.array());
            System.out.println("endPacket: " + Arrays.toString(endPacket));
            dataOutputStream.write(endPacket, 0, endPacket.length);
            dataOutputStream.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void storeFile(byte[] body) {
        fileManager.store(body);
    }

    private void combineFile(byte[] fileName) {
        ByteBuffer fileNameBuffer = ByteBuffer.wrap(fileName);
        int fileNameLength = fileNameBuffer.getInt();
        byte[] fileNameByte = new byte[fileNameLength];
        fileNameBuffer.get(fileNameByte);

        byte[] totalFile = fileManager.getCombineFile(fileNameByte);
        saveFile(totalFile);
    }

    private void saveFile(byte[] body) {
        try {
            String randomFileName = UUID.randomUUID() + ".txt";
            String directoryPath = "C:\\Users\\yangd\\OneDrive\\바탕 화면\\filetest\\outputPath";
            // home String directoryPath = "C:\\Users\\user\\Desktop\\BEmetoring\\file_test\\output";
            File file = new File(directoryPath, randomFileName);
            FileOutputStream fileOutputStream =  new FileOutputStream(file);
            fileOutputStream.write(body);
            fileOutputStream.flush();
            System.out.println("File saved successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRegister(String command) {
        byte[] packet = HeaderAdder.addHeader(MessageType.REGISTER_ID, command.substring(3).getBytes());
        sendPacket(packet);
    }

    private void processFin() {
        System.out.println("process fin start");
        byte[] packet = HeaderAdder.addOnlyTypeHeader(MessageType.FIN);
        sendPacket(packet);
    }

    private void processChangeId(String command) {
        byte[] packet = HeaderAdder.addHeader(MessageType.CHANGE_ID, command.substring(3).getBytes());
        sendPacket(packet);
    }

    private void processWhisper(String command) {
        System.out.println("start whisper: " + Arrays.toString(command.substring(3).getBytes()));
        byte[] packet = HeaderAdder.addHeader(MessageType.WHISPER, command.substring(3).getBytes());
        sendPacket(packet);
    }

    private void processComment(String command) {
        byte[] packet = HeaderAdder.addHeader(MessageType.COMMENT, command.getBytes());
        sendPacket(packet);
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

}