    package org.example;

    import lombok.Data;
    import org.junit.jupiter.api.Assertions;
    import org.junit.jupiter.api.Test;
    import org.mockito.*;

    import java.io.*;
    import java.net.Socket;
    import java.nio.ByteBuffer;
    import java.util.*;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;

    class ClientTest {

    //    @Test
    //    void testSendRegisterIdType() throws IOException {
    //        // Given
    //        // telecomunicate with Sercer
    //        // serverMock
    //        Server mockServer = Mockito.mock(Server.class);
    //        // Client real
    //        Client client = new Client();
    //
    //        // When
    //        String command = "/r myId";
    //        client.processCommand(command);
    //
    //    }

        @Test
        public void testExtractListInCommand() {
            // Given
            CommandProcessor commandProcessor = new CommandProcessor();
            String command = "/r myId";

            //When
            ArrayList<Object> result = commandProcessor.extract(command);
            MessageTypeCode messageTypeCode = (MessageTypeCode) result.get(0);
            RegisterIdType registerIdType = (RegisterIdType) result.get(1);

            // Then
            Assertions.assertEquals(2, result.size());
            Assertions.assertEquals(MessageTypeCode.REGISTER_ID, messageTypeCode);
            assertTrue(result.get(1) instanceof RegisterIdType);
            Assertions.assertEquals("myId", registerIdType.getId());

        }

        @Test
        public void testMakeRegisterIdPacket() throws IOException {
            // Given
            CommandProcessor commandProcessor = new CommandProcessor();
            String command = "/r myId";

            // When
            ArrayList<Object> result = commandProcessor.extract(command);
            byte[] expectedPacket = createExpectedPacketData();
            byte[] packet = PacketMaker.makePacket((MessageTypeCode) result.get(0), (MessageType) result.get(1));

            // then
            assertArrayEquals(expectedPacket, packet, "not match expected");

        }

        private byte[] createExpectedPacketData() throws IOException {
            MessageType messageType = new RegisterIdType("myId");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(messageType);
            objectOutputStream.flush();
            byte[] body = byteArrayOutputStream.toByteArray();

            int bodyLengthSize = Integer.BYTES;
            int typeIntSize = Integer.BYTES;
            ByteBuffer byteBuffer = ByteBuffer.allocate(bodyLengthSize + typeIntSize + body.length);
            byteBuffer.putInt(body.length);
            byteBuffer.putInt(0);
            byteBuffer.put(body);

            return byteBuffer.array();
        }

        @Test
        public void testCorrectPacketReceiveInServer() throws IOException {
            //Given
            Socket mockClientSocket = mock(Socket.class);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            when(mockClientSocket.getOutputStream()).thenReturn(byteArrayOutputStream);
            Client client = new Client(mockClientSocket);

            MessageType clientSendMessageType = new RegisterIdType("myId");


            // when
            client.processCommand("/r myId");
            byte[] sentData = byteArrayOutputStream.toByteArray();
            System.out.println("in test\nsentData: " + Arrays.toString(sentData));

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sentData);
            Socket mockServerSocket = mock(Socket.class);
            Server mockServer = mock(Server.class);
            when(mockServerSocket.getInputStream()).thenReturn(byteArrayInputStream);

            ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

            ClientHandler clientHandler = new ClientHandler(mockServer, mockServerSocket);
            clientHandler.run();

            // then
            verify(mockServer).service(messageCaptor.capture());
            Message capturedMessage = messageCaptor.getValue();
            System.out.println("message body: " + Arrays.toString(capturedMessage.getBody()));
            MessageType actualMessageType = MessageProcessor.makeMessageType(capturedMessage);

            assertEquals(clientSendMessageType, actualMessageType);



        }


    }