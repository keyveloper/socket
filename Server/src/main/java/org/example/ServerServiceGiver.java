package org.example;

import lombok.Data;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

@Data
public class ServerServiceGiver implements ServiceGiver{
    private final Server server;

    // message -> InputMessageProccessor가 해결 -> 이거 사실 서버에서 해도됨
    // 서버에서 하는걸로 하고 -> server에서 meesageType객체와, 소켓을 넘겨주면 되잖소?
    @Override
    // 소켓 정보가 필요하기 떄문에, message가 넘어오는게 좋음
    public void service(Message message, MessageType messageType) throws IOException {
        System.out.println("service start: " + messageType.toString());
        ClientHandler clientHandler = server.getHandlerManger().get(message.getClientSocket());
        switch (message.getMessageTypeCode()) {
            case REGISTER_ID:
                RegisterIdStatusType registerIdStatusType = registerIdService((RegisterIdType) messageType, message.getClientSocket());
                System.out.println("service type object: " + registerIdStatusType.toString());
                clientHandler.sendPacket(PacketMaker.makePacket(message.getMessageTypeCode(), messageType));
                break;
        }
    }

    public RegisterIdStatusType registerIdService(RegisterIdType registerIdType, Socket socket) {
        String id = registerIdType.getId();
        IdManager idManager = server.getIdManager();
        CountManager countManager = server.getCountManager();
        RegisterIdStatusType registerIdStatusType = idManager.register(id, socket);
        countManager.register(socket);

        return registerIdStatusType;
    }
}

