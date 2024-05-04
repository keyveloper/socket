package org.example;

import lombok.Data;
import org.example.types.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

@Data
public class ServerServiceGiver implements ServiceGiver{
    private final HandlerManger handlerManger;
    private final IdManager idManager = new IdManager();
    private final CountManager countManager = new CountManager();
    private final FileTokenManger fileTokenManger = new FileTokenManger();

    // message -> InputMessageProccessor가 해결 -> 이거 사실 서버에서 해도됨
    // 서버에서 하는걸로 하고 -> server에서 meesageType객체와, 소켓을 넘겨주면 되잖소?
    @Override
    // 소켓 정보가 필요하기 떄문에, message가 넘어오는게 좋음
    public void service(Message message, MessageType messageType) {
        ClientHandler senderHandler = handlerManger.get(message.getSender());
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
            case File_START_INFO:
                sendFileToken((FileStartInfo) messageType, message.getSender());
                break;
            case FILE:
                sendFile((FileType) messageType);
                break;
            case FILE_END:
                sendFileEnd((FileEndType) messageType, message.getSender());
                break;
        }
    }

    private void sendFileToken(FileStartInfo fileStartInfo, Socket sender) {
        FileToken fileToken = makeFileToken(fileStartInfo.getSender(), fileStartInfo.getReceiver(), fileStartInfo.getFileId());
        ClientHandler senderHandler = handlerManger.get(sender);
        senderHandler.sendPacket(PacketMaker.makePacket(MessageTypeCode.File_Token, fileToken));
    }

    private FileToken makeFileToken(String sender, String receiver, UUID fileId) {
        UUID tokenId = UUID.randomUUID();
        saveToken(tokenId, sender, receiver);
        return new FileToken(tokenId, fileId);
    }

    private void saveToken(UUID tokenId, String sender, String receiver) {
        HashMap<String, Socket> actorSocketMap = new HashMap<>();
        actorSocketMap.put("sender", idManager.getSocketById(sender));
        actorSocketMap.put("receiver", idManager.getSocketById(receiver));
        fileTokenManger.put(tokenId, actorSocketMap);
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

        ClientHandler senderHandler = handlerManger.get(sender);
        senderHandler.sendPacket(PacketMaker.makePacket(MessageTypeCode.REGISTER_STATUS, changeStatusType));

        if (changeStatusType.getIsSuccess()) {
            sendIdChangeToAll(changeIdType.getOldId(), changeIdType.getNewId());
        }
    }

    private void sendFileEnd(FileEndType fileEndType, Socket sender) {
        Socket receiverSocket = idManager.getSocketById(fileEndType.getId());
        ClientHandler receiverHandler = handlerManger.get(receiverSocket);

        FileEndType endType = new FileEndType(idManager.getIdBySocket(sender), fileEndType.getFileName());
        receiverHandler.sendPacket(PacketMaker.makePacket(MessageTypeCode.FILE_END, endType));
    }

    private RegisterIdStatusType changeId(String newId, Socket socket) {
        return idManager.changeId(newId, socket);
    }

    private void sendWhisper(String receiverId, String comment, Socket senderSocket){
        Socket receiverSocket = idManager.getSocketById(receiverId);
        String senderId = idManager.getIdBySocket(senderSocket);
        ClientHandler receiverHandler = handlerManger.get(receiverSocket);
        receiverHandler.sendPacket(PacketMaker.makePacket(MessageTypeCode.WHISPER, new WhisperType(senderId, comment)));

        plusCount(senderSocket);
    }

    private void plusCount(Socket sender) {
        countManager.add(sender);
    }

    private void sendComment(String comment, Socket senderSocket)  {
        String senderId = idManager.getIdBySocket(senderSocket);
        ArrayList<ClientHandler> handlers = handlerManger.getAllHandler();
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
        System.out.println("closeConnect start: " + senderSocket);
        countManager.print();

        String closeId = idManager.getIdBySocket(senderSocket);
        int count = countManager.get(senderSocket);
        String message = "ID: " + closeId + "is out\n total message: " + count;

        idManager.remove(senderSocket);
        countManager.remove(senderSocket);
        handlerManger.remove(senderSocket);

        sendNoticeToAll(new NoticeType(NoticeCode.FIN, message));
    }
    private void sendFile(FileType fileType) {
        Socket receiverSocket = fileTokenManger.getReceiver(fileType.getTokenId());
        handlerManger.get(receiverSocket).sendPacket(PacketMaker.makePacket(MessageTypeCode.FILE, fileType));
    }

    private void sendIdChangeToAll(String oldId, String newId) {
        ArrayList<ClientHandler> handlers = handlerManger.getAllHandler();
        ChangeIdType changeIdType = new ChangeIdType(oldId, newId);
        for (ClientHandler handler : handlers) {
            handler.sendPacket(PacketMaker.makePacket(MessageTypeCode.CHANGE_ID, changeIdType));
        }

    }
    private void sendNoticeToAll(NoticeType noticeType) {
        ArrayList<ClientHandler> handlers = handlerManger.getAllHandler();
        byte[] noticePacket = PacketMaker.makePacket(MessageTypeCode.NOTICE, noticeType);
        for (ClientHandler handler : handlers) {
            handler.sendPacket(noticePacket);
        }
    }


    }

