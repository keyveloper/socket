package org.example;

import javax.swing.text.AttributeSet;
import javax.swing.text.FieldView;
import javax.swing.text.Style;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
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
        long threadId = Thread.currentThread().getId();
        System.out.println("myid: "+  threadId);
        try {
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", tcpClientPort));
            System.out.print("\n [ Success connecting ] \n");

            while (true) {
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                int bodyLengthSize= 4;
                int typeLengthSize = 4;
                int inAllLength = dataInputStream.readInt();
                if(inAllLength > 0) {
                    byte[] inLengthByte = new byte[4];
                    dataInputStream.readFully(inLengthByte);
                    int messageLength = Share.readInputLength(inLengthByte);
                    System.out.println("received meesageLenght: " + messageLength);

                    byte[] inTypeByte = new byte[4];
                    dataInputStream.readFully(inTypeByte);
                    int typeInt = ByteBuffer.wrap(inTypeByte).getInt();
                    MessageType inMessageType = MessageType.values()[typeInt];
                    System.out.println("received type: " + inMessageType);

                    byte[] inMessageByte = new byte[inAllLength - bodyLengthSize - typeLengthSize];
                    dataInputStream.readFully(inMessageByte);
                    System.out.println("inMessageByte: " + Arrays.toString(inMessageByte));

                    Message message = new Message(inMessageType, inMessageByte);
                    processMessage(message);

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
            return;
        }
        if (!registered) {
            System.out.println("Register first!! \n /R or /r id");
            return;
        }
        if (command.startsWith("/Q") || command.startsWith("/q")) {
            processFin();
            return;
        }
        if (command.startsWith("/N") || command.startsWith("/n")) {
            processChangeId(command);
            return;
        }
        if (command.startsWith("/W") || command.startsWith("/w")) {
            processWhisper(command);
            return;
        }
        if (command.startsWith("/F") || command.startsWith("/f")) {
            processFile(command);
            return;
        }
        processComment(command);
    }

    private void processMessage(Message message) {
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
            case FILE_END:
                combineFile(message.getBody());
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
        String filePath = "C:\\Users\\user\\Desktop\\BEmetoring\\file_test";
        File file = new File(filePath, new String(fileName));
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] readByte = new byte[10];
            String onlyName = new String(fileName).split("\\.")[0];
            int receiverLengthSize = receiver.length;
            int fileNameSize = 4;
            int seqSize = 4;
            int byteRead;
            int seq = 0;
            while ((byteRead = fileInputStream.read(readByte)) != -1) {
                byte[] actualRead = Arrays.copyOf(readByte, byteRead);

                ByteBuffer bodyBuffer = ByteBuffer.allocate(receiverLengthSize + receiver.length + fileName.length + seqSize + actualRead.length);
                System.out.println("want to " + 4 + receiver.length + 4 + 4 + actualRead.length);
                System.out.println("bodybuffer size: " + bodyBuffer.capacity());
                bodyBuffer.putInt(receiver.length);
                bodyBuffer.put(receiver);
                bodyBuffer.putInt(fileNameSize);
                bodyBuffer.put(onlyName.getBytes());
                bodyBuffer.putInt(seq);
                bodyBuffer.put(actualRead);

                byte[] packet = HeaderMaker.makeHeader(MessageType.FILE, bodyBuffer.array());
                System.out.println("made packet: " + Arrays.toString(packet));
                dataOutputStream.write(packet,0, packet.length);
                dataOutputStream.flush();
                seq ++;
            }
            // System.out.println("all filePacket be sent");
            ByteBuffer endBuffer = ByteBuffer.allocate(receiver.length + fileName.length);
            byte[] endPacket = HeaderMaker.makeHeader(MessageType.FILE_END, endBuffer.array());
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
        byte[] totalFile = fileManager.getCombineFile(fileName);
        saveFile(totalFile);
    }

    private void saveFile(byte[] body) {
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

    private void processRegister(String command) {
        byte[] packet = HeaderMaker.makeHeader(MessageType.REGISTER_ID, command.substring(3).getBytes());
        sendPacket(packet);
    }

    private void processFin() {
        System.out.println("process fin start");
        byte[] packet = HeaderMaker.makeOnlyTypeHeader(MessageType.FIN);
        sendPacket(packet);
    }

    private void processChangeId(String command) {
        byte[] packet = HeaderMaker.makeHeader(MessageType.CHANGE_ID, command.substring(3).getBytes());
        sendPacket(packet);
    }

    private void processWhisper(String command) {
        System.out.println("start whisper: " + Arrays.toString(command.substring(3).getBytes()));
        byte[] packet = HeaderMaker.makeHeader(MessageType.WHISPER, command.substring(3).getBytes());
        sendPacket(packet);
    }

    private void processComment(String command) {
        byte[] packet = HeaderMaker.makeHeader(MessageType.COMMENT, command.getBytes());
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