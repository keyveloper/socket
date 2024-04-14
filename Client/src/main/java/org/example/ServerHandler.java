package org.example;

import lombok.Data;


@Data
public class ServerHandler implements Runnable{
    private final Client client;
    @Override
    public void run() {
        ClientPacketReader clientPacketReader = new ClientPacketReader(client.getSocket());
        while (true) {
            Message message = clientPacketReader.readPacket();
            client.service(message);
        }
    }

    public void sendPacket(byte[] packet) {

    }

}
