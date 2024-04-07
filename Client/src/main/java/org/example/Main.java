package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            Socket socket = new Socket();
            Client client = new Client(socket);
            Thread clientThread = new Thread(client);
            clientThread.start();

            while (true){
                String command = bufferedReader.readLine();
                client.processCommand(command);
                if (command.startsWith("/Q")) {
                    break;
                }
            }
            clientThread.join();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}