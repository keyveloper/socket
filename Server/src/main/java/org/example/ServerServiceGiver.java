package org.example;

import com.sun.source.tree.Scope;
import lombok.Data;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
        ClientHandler senderHandler = server.getHandlerManger().get(message.getClientSocket());
        switch (message.getMessageTypeCode()) {
            case REGISTER_ID:
                RegisterIdType registerIdType = (RegisterIdType) messageType;
                RegisterIdStatusType RegisterIdstatusType = registerId(registerIdType.getId(), message.getClientSocket());
                System.out.println("service type object: " + RegisterIdstatusType);
                senderHandler.sendPacket(PacketMaker.makePacket(MessageTypeCode.REGISTER_STATUS, RegisterIdstatusType));
                break;
            case CHANGE_ID:
                ChangeIdType changeIdType = (ChangeIdType) messageType;
                RegisterIdStatusType changeStatusType = changeId(changeIdType.getChangeId(), message.getClientSocket());
                System.out.println("change Success");
                senderHandler.sendPacket(PacketMaker.makePacket(MessageTypeCode.REGISTER_STATUS, changeStatusType));
                break;
            case WHISPER:
                WhisperType whisperType = (WhisperType) messageType;
                sendWhisper(whisperType.getId(), whisperType.getComment(), message.getClientSocket());
                break;
            case COMMENT:
                CommentType commentType = (CommentType) messageType;
                sendComment(commentType.getComment(), message.getClientSocket());
                break;
            case FIN:
                closeConnect(message.getClientSocket());
                break;
            case FILE:
                sendFile((FileType) messageType);
        }
    }

    private RegisterIdStatusType registerId(String id, Socket socket) {
        IdManager idManager = server.getIdManager();
        RegisterIdStatusType registerIdStatusType = idManager.register(id, socket);
        if (registerIdStatusType.getIsSuccess()) {
            CountManager countManager = server.getCountManager();
            countManager.register(socket);
        }
        return registerIdStatusType;
    }

    private RegisterIdStatusType changeId(String newId, Socket socket) {
        IdManager idManager = server.getIdManager();
        return idManager.changeId(newId, socket);
    }

    private void sendWhisper(String receiverId, String comment, Socket senderSocket) throws IOException {
        Socket receiverSocket = server.getIdManager().getSocketById(receiverId);
        String senderId = server.getIdManager().getIdBySocket(senderSocket);
        ClientHandler receiverHandler = server.getHandlerManger().get(receiverSocket);
        receiverHandler.sendPacket(PacketMaker.makePacket(MessageTypeCode.WHISPER, new WhisperType(senderId, comment)));

        plusCount(senderSocket);
    }

    private void plusCount(Socket sender) {
        server.getCountManager().add(sender);
    }

    private void sendComment(String comment, Socket senderSocket) throws IOException {
        String senderId = server.getIdManager().getIdBySocket(senderSocket);
        ArrayList<ClientHandler> handlers = server.getHandlerManger().getAllHandler();
        CommentType commentType = new CommentType(comment);
        commentType.setSenderId(senderId);
        System.out.println("sendComment: " + commentType);
        byte[] commentPacket = PacketMaker.makePacket(MessageTypeCode.COMMENT, commentType);
        System.out.println("commentPacket: " + Arrays.toString(commentPacket));

        for (ClientHandler handler : handlers) {
            handler.sendPacket(commentPacket);
        }

        plusCount(senderSocket);
    }

    private void closeConnect(Socket senderSocket) throws IOException {
        String closeId = server.getIdManager().getIdBySocket(senderSocket);
        int count = server.getCountManager().get(senderSocket);
        String message = "ID: " + closeId + "is out\n total message: " + count;

        server.getIdManager().remove(senderSocket);
        server.getCountManager().remove(senderSocket);
        server.getHandlerManger().remove(senderSocket);

        ArrayList<ClientHandler> handlers = server.getHandlerManger().getAllHandler();
        byte[] commentPacket = PacketMaker.makePacket(MessageTypeCode.NOTICE, new NoticeType(message));
        for (ClientHandler handler : handlers) {
            handler.sendPacket(commentPacket);
        }
    }
    private void sendFile(FileType fileType) throws IOException{
        Socket receiverSocket = server.getIdManager().getSocketById(fileType.getReceiver());
        server.getHandlerManger().get(receiverSocket).sendPacket(PacketMaker.makePacket(MessageTypeCode.FILE, fileType));
    }

}

