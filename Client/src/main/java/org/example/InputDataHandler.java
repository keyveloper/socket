package org.example;
import java.io.*;
import java.net.Socket;

public class InputDataHandler implements Runnable {
    private final Client client;
    private final Socket socket;

    public InputDataHandler(Client client, Socket socket){
        this.client = client;
        this.socket = socket;
        System.out.println("input handler start");
    }
    @Override
    public void run(){
        try {
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            while (true) {
                int inAllLength = dataInputStream.readInt();
                if(inAllLength > 0){
                    //[]
                    byte[] inLengthByte = new byte[4];
                    dataInputStream.readFully(inLengthByte);
                    int messageLength = Share.readInputLength(inLengthByte);

                    byte[] inTypeByte = new byte[4];
                    dataInputStream.readFully(inTypeByte);
                    MessageType inMessageType = Share.readInputType(inTypeByte);

                    byte[] inMessageByte = new byte[inAllLength - 8];
                    dataInputStream.readFully(inMessageByte);
                    String message = Share.readInputMessage(inMessageByte);

                    actionByType(inMessageType, message);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void actionByType(MessageType inputType, String message){
        switch (inputType){
            case COMMENT:
                client.printInputData("\n" + message + "\n");
                break;
            case NOTICE:
                client.printInputData("\n" + message + "\n");
                break;
            case ALREADY_EXIST_ID:
                client.printInputData("This ID already Exist");
                break;
            case REGISTER_SUCCESS:
                client.printInputData("Register Success!!");
                client.registered = true;
                break;
        }
    }

}
