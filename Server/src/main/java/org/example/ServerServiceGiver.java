package org.example;

import lombok.Data;
import org.example.types.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

@Data
public class ServerServiceGiver implements ServiceGiver{
    private final Server server;
    private final IdManager idManager;
    private final CountManager countManager;

    // message -> InputMessageProccessor가 해결 -> 이거 사실 서버에서 해도됨
    // 서버에서 하는걸로 하고 -> server에서 meesageType객체와, 소켓을 넘겨주면 되잖소?
    @Override
    // 소켓 정보가 필요하기 떄문에, message가 넘어오는게 좋음
    public void service(Message message, MessageType messageType) {
        System.out.println("service start: " + messageType.toString());
        ClientHandler senderHandler = server.getHandlerManger().get(message.getSender());
        switch (message.getMessageTypeCode()) {
            case REGISTER_ID:
                RegisterIdType registerIdType = (RegisterIdType) messageType;
                RegisterIdStatusType RegisterIdstatusType = registerId(registerIdType.getId(), message.getSender());
                System.out.println("service type object: " + RegisterIdstatusType);
                senderHandler.sendPacket(PacketMaker.makePacket(MessageTypeCode.REGISTER_STATUS, RegisterIdstatusType));
                break;
            case CHANGE_ID:
                changeId((ChangeIdType) messageType, message.getSender());
                break;
            case WHISPER:
                WhisperType whisperType = (WhisperType) messageType;
                sendWhisper(whisperType.getId(), whisperType.getComment(), message.getSender());
                break;
            case COMMENT:
                CommentType commentType = (CommentType) messageType;
                sendComment(commentType.getComment(), message.getSender());
                break;
            case FIN:
                closeConnect(message.getSender());
                break;
            case FILE_START:
                sendFileStart((FileStartType) messageType, message.getSender());
                break;
            case FILE:
                sendFile((FileType) messageType);
                break;
            case FILE_END:
                sendFileEnd((FileEndType) messageType, message.getSender());
        }
    }

    private RegisterIdStatusType registerId(String id, Socket sender) {
        RegisterIdStatusType registerIdStatusType = idManager.register(id, sender);
        if (registerIdStatusType.getIsSuccess()) {
            countManager.register(sender);
        }
        return registerIdStatusType;
    }

    private void changeId(ChangeIdType changeIdType, Socket sender) {
        RegisterIdStatusType changeStatusType = changeId(changeIdType.getNewId(), sender);

        ClientHandler senderHandler = server.getHandlerManger().get(sender);
        senderHandler.sendPacket(PacketMaker.makePacket(MessageTypeCode.REGISTER_STATUS, changeStatusType));

        if (changeStatusType.getIsSuccess()) {
            sendIdChangeToAll(changeIdType.getOldId(), changeIdType.getNewId());
        }
    }

    private void sendFileStart(FileStartType fileStartType, Socket sender) {
        Socket receiverSocket = idManager.getSocketById(fileStartType.getId());
        ClientHandler receiverHandler = server.getHandlerManger().get(receiverSocket);

        receiverHandler.sendPacket(PacketMaker.makePacket(MessageTypeCode.FILE_START, new FileStartType(idManager.getIdBySocket(sender), fileStartType.getFileName())));
    }

    private void sendFileEnd(FileEndType fileEndType, Socket sender) {
        Socket receiverSocket = idManager.getSocketById(fileEndType.getId());
        ClientHandler receiverHandler = server.getHandlerManger().get(receiverSocket);

        receiverHandler.sendPacket(PacketMaker.makePacket(MessageTypeCode.FILE_END, new FileEndType(idManager.getIdBySocket(sender), fileEndType.getFileName())));
    }

    private RegisterIdStatusType changeId(String newId, Socket socket) {
        return idManager.changeId(newId, socket);
    }

    private void sendWhisper(String receiverId, String comment, Socket senderSocket){
        Socket receiverSocket = idManager.getSocketById(receiverId);
        String senderId = idManager.getIdBySocket(senderSocket);
        ClientHandler receiverHandler = server.getHandlerManger().get(receiverSocket);
        receiverHandler.sendPacket(PacketMaker.makePacket(MessageTypeCode.WHISPER, new WhisperType(senderId, comment)));

        plusCount(senderSocket);
    }

    private void plusCount(Socket sender) {
        countManager.add(sender);
    }

    private void sendComment(String comment, Socket senderSocket)  {
        String senderId = idManager.getIdBySocket(senderSocket);
        ArrayList<ClientHandler> handlers = server.getHandlerManger().getAllHandler();
        CommentType commentType = new CommentType(senderId, comment);
        System.out.println("sendComment: " + commentType);
        byte[] commentPacket = PacketMaker.makePacket(MessageTypeCode.COMMENT, commentType);
        System.out.println("commentPacket: " + Arrays.toString(commentPacket));

        for (ClientHandler handler : handlers) {
            handler.sendPacket(commentPacket);
        }

        plusCount(senderSocket);
    }

    private void closeConnect(Socket senderSocket) {
        String closeId = idManager.getIdBySocket(senderSocket);
        int count = countManager.get(senderSocket);
        String message = "ID: " + closeId + "is out\n total message: " + count;

        idManager.remove(senderSocket);
        countManager.remove(senderSocket);
        server.getHandlerManger().remove(senderSocket);

        sendNoticeToAll(new NoticeType(NoticeCode.FIN, message));
    }
    private void sendFile(FileType fileType) {
        Socket receiverSocket = idManager.getSocketById(fileType.getReceiver());
        server.getHandlerManger().get(receiverSocket).sendPacket(PacketMaker.makePacket(MessageTypeCode.FILE, fileType));
    }

    private void sendIdChangeToAll(String oldId, String newId) {
        ArrayList<ClientHandler> handlers = server.getHandlerManger().getAllHandler();
        ChangeIdType changeIdType = new ChangeIdType(oldId, newId);
        for (ClientHandler handler : handlers) {
            handler.sendPacket(PacketMaker.makePacket(MessageTypeCode.CHANGE_ID, changeIdType));
        }

    }
    private void sendNoticeToAll(NoticeType noticeType) {
        ArrayList<ClientHandler> handlers = server.getHandlerManger().getAllHandler();
        byte[] noticePacket = PacketMaker.makePacket(MessageTypeCode.NOTICE, noticeType);
        for (ClientHandler handler : handlers) {
            handler.sendPacket(noticePacket);
        }
    }


    }

