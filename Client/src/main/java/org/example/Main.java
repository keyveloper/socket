package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread client = new Thread(new Client());
        client.start();
    }
}