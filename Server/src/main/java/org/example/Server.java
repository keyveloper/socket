package org.example;

import java.io.*;
import java.net.*;

public class Server {
    public static int tcpServerPort = 9999;
    private HashMap<String, Integer> idList;
    private HahsMap<String, Integer> messageCount;

    public static void main(String[] args) {
        new Server(tcpServerPort);
    }
    public Server(int portNo){
        tcpServerPort = portNo;
        try{
            ServerSocket serverSocket = new SeverSocket();
            serverSocket.bind(new InetSocketAddress(tcpServerPort));
            System.out.println("Starting tcp Server: ", tcpServerPort);
            System.out.println("\n [Waiting] \n");
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("Connected" + socket.getLocalPort() + "Port, From" + socket.getRemoteSocketAddress() + "\n");
                TcpServer tcpServer = new tcpServer(socket);
                tcpServer.start();
            }
        } catch(IOException io){
            io.getStackTrace();
        }
    }

    public class TcpServer implements Runnable{
        private Socket socket;

        public TcpServer(Socket socket){
            this.socket = socket
        }

        public void run(){
            try{
                while (true){
                    OutputStream outputStream = this.socket.getOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                    InputStream inputStream = this.socket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);

                    // receive length bytes
                    byte receivedLengthByte[] = new byte[4];
                    dataInputStream.readFully(receivedLengthByte);
                    int receivedLengthInt = ByteBuffer.wrap(receivedLengthByte).getInt();
                    System.out.println("type int: " + receivedLengthInt);

                    // receive Type bytes
                    byte receivedTypeByte[] = new byte[4];
                    int receivedTypeInt = ByteBuffer.wrap(receivedTypeByte).getInt();
                    dataInputStream.readFully(receivedTypeByte);

                    System.out.println("type int: " + receivedTypeInt);

                    // receive real message
                    byte[] receivedMessageByte = new byte[recieveLength - 8];
                    dataInputStream.readFully(receivedMessageByte);
                    String receiveMessage = new String(receivedMessageByte);
                    System.out.println("receiveMessage : " + receiveMessage);
                    System.out.println("[ Data Receive Success ]\n");

                    // send bytes
                    String sendMessage = "nice to meet you, " + receiveMessage + "!!";
                    byte[] sendBytes = sendMessage.getBytes("UTF-8");
                    int sendLength = sendBytes.length;
                    dataOutputStream.writeInt(sendLength);
                    dataOutputStream.write(sendBytes, 0, sendLength);
                    dataOutputStream.flush();

                    System.out.println("sendMessage : " + sendMessage);
                    System.out.println("[ Data Send Success ]");

                }
            }
        }
    }

}
