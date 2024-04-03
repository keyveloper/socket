package org.example;

import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

@Data
public class PacketMaker {
    public static byte[] makePacket(MessageTypeCode typeCode, HashMap<String, Object> contentMap) throws IOException {
        // why null
        MessageType messageType = null;
        switch (typeCode) {
            case REGISTER_ID:
                messageType = new RegisterIdTypeMaker(contentMap.get("id").toString()).make();
                break;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(byteArrayOutputStream);
        objectOutputStream.flush();
        byte[] body = byteArrayOutputStream.toByteArray();
        return HeadAdder.add(typeCode, body);
    }

}
