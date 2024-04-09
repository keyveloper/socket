    package org.example;


    import org.junit.jupiter.api.Test;
    import org.mockito.*;

    import java.io.*;
    import java.net.Socket;
    import java.nio.ByteBuffer;
    import java.util.*;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;

    class ClientTest {

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