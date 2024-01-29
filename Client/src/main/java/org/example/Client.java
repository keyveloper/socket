package org.example;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import org.example.MessageType;
import org.example.Share;


public class ClientCopy {

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        Socket socket = null;
        Boolean register = false;

        try {
            // Server
            socket = new Socket();
            System.out.println("\n[ Request ... ]");
            // Server
            socket.connect(new InetSocketAddress("localhost", 9999));
            System.out.println("\n[ Success ... ]");

            byte[] finalBBytes = null;
            String message = null;

            while (true) { // Socket
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);

                // send bytes
                message = bufferedReader.readLine();
                String messageType = bufferedReader.readLine();

                byte[] messageBytes = message.getBytes();
                byte[] finalIdBytes = Share.plusHeader(messageBytes, messageType);

                dos.writeInt(finalIdBytes.length);
                dos.write(finalIdBytes, 0, finalIdBytes.length);
                dos.flush();

                System.out.println("\n[ Data Send Success ]\n" + message);

                // Socket
                InputStream is = socket.getInputStream();
                DataInputStream dis = new DataInputStream(is);

                // read int
                int receiveLength = dis.readInt();

                // receive bytes
                if (receiveLength > 0) {
                    byte receiveByte[] = new byte[receiveLength];
                    dis.readFully(receiveByte, 0, receiveLength);
                    message = new String(receiveByte);
                    System.out.println("\n[ Data Receive Success ]\n" + message);
                    register = true;
                }

                // OutputStream, InputStream close

                // Socket}


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}