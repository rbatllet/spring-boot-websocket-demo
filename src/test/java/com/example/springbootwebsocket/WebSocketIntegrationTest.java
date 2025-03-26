package com.example.springbootwebsocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private String wsUrl;
    private StandardWebSocketClient webSocketClient;

    @BeforeEach
    void setup() {
        wsUrl = "ws://localhost:" + port + "/chat";
        webSocketClient = new StandardWebSocketClient();
    }

    @Test
    void testWebSocketConnection() throws Exception {
        // Create a future to wait for the connection to be established
        CompletableFuture<Boolean> connectionEstablished = new CompletableFuture<>();

        // Connect to the WebSocket endpoint
        WebSocketSession session = webSocketClient.execute(
                new TextWebSocketHandler() {
                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) {
                        connectionEstablished.complete(true);
                    }
                },
                new WebSocketHttpHeaders(),
                URI.create(wsUrl)
        ).get(5, TimeUnit.SECONDS);

        // Verify that the connection was established
        assertTrue(connectionEstablished.get(5, TimeUnit.SECONDS));

        // Clean up
        session.close();
    }

    @Test
    void testSendAndReceiveMessage() throws Exception {
        // Create a future to store the received message
        AtomicReference<String> receivedMessageRef = new AtomicReference<>();
        CompletableFuture<String> messageReceived = new CompletableFuture<>();

        // Connect client1 (sender)
        WebSocketSession senderSession = webSocketClient.execute(
                new TextWebSocketHandler() {
                    @Override
                    public void handleTextMessage(WebSocketSession session, TextMessage message) {
                        // Store the received message
                        receivedMessageRef.set(message.getPayload());
                        messageReceived.complete(message.getPayload());
                    }
                },
                new WebSocketHttpHeaders(),
                URI.create(wsUrl)
        ).get(5, TimeUnit.SECONDS);

        // Connect client2 (receiver)
        WebSocketSession receiverSession = webSocketClient.execute(
                new TextWebSocketHandler() {
                    @Override
                    public void handleTextMessage(WebSocketSession session, TextMessage message) {
                        // Store the received message
                        receivedMessageRef.set(message.getPayload());
                        messageReceived.complete(message.getPayload());
                    }
                },
                new WebSocketHttpHeaders(),
                URI.create(wsUrl)
        ).get(5, TimeUnit.SECONDS);

        // Wait a bit to ensure both connections are established
        Thread.sleep(500);

        // Send a message from client1
        String messageText = "{\"name\":\"TestUser\",\"message\":\"Hello from test!\"}";
        senderSession.sendMessage(new TextMessage(messageText));

        // Wait for the message to be received
        String receivedMessage = messageReceived.get(5, TimeUnit.SECONDS);

        // Verify that the message was received correctly
        assertEquals(messageText, receivedMessage);

        // Clean up
        senderSession.close();
        receiverSession.close();
    }

    @Test
    void testBroadcastToMultipleClients() throws Exception {
        // Create futures to track message receipt
        CompletableFuture<String> client1MessageReceived = new CompletableFuture<>();
        CompletableFuture<String> client2MessageReceived = new CompletableFuture<>();
        CompletableFuture<String> client3MessageReceived = new CompletableFuture<>();

        // Connect client1
        WebSocketSession client1Session = connectTestClient(client1MessageReceived);

        // Connect client2
        WebSocketSession client2Session = connectTestClient(client2MessageReceived);

        // Connect client3
        WebSocketSession client3Session = connectTestClient(client3MessageReceived);

        // Wait a bit to ensure all connections are established
        Thread.sleep(500);

        // Send a message from client1
        String messageText = "{\"name\":\"BroadcastTest\",\"message\":\"Hello everyone!\"}";
        client1Session.sendMessage(new TextMessage(messageText));

        // Wait for all clients to receive the message
        String client1Received = client1MessageReceived.get(5, TimeUnit.SECONDS);
        String client2Received = client2MessageReceived.get(5, TimeUnit.SECONDS);
        String client3Received = client3MessageReceived.get(5, TimeUnit.SECONDS);

        // Verify that all clients received the same message
        assertEquals(messageText, client1Received);
        assertEquals(messageText, client2Received);
        assertEquals(messageText, client3Received);

        // Clean up
        client1Session.close();
        client2Session.close();
        client3Session.close();
    }

    private WebSocketSession connectTestClient(CompletableFuture<String> messageReceived) 
            throws InterruptedException, ExecutionException, TimeoutException {
        return webSocketClient.execute(
                new TextWebSocketHandler() {
                    @Override
                    public void handleTextMessage(WebSocketSession session, TextMessage message) {
                        messageReceived.complete(message.getPayload());
                    }
                },
                new WebSocketHttpHeaders(),
                URI.create(wsUrl)
        ).get(5, TimeUnit.SECONDS);
    }
}
