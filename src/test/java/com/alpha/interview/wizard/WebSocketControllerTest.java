package com.alpha.interview.wizard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.alpha.interview.wizard.controller.WebSocketController;
import com.alpha.interview.wizard.model.Message;

import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketControllerTest {

    @LocalServerPort
    private int port;

    @Test
    public void testWebSocketController() throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        CountDownLatch latch = new CountDownLatch(1);
        Message message = new Message("User", "World");
        StompSessionHandlerAdapter sessionHandler = new StompSessionHandlerAdapter() {
        	@Override
        	public void handleFrame(StompHeaders headers, Object payload) {
        	    Message receivedPayload = (Message) payload;
        	    // // System.out.println("Received payload: " + receivedPayload);
        	    latch.countDown();
        	}
        	@Override
        	public void handleException(StompSession session, StompCommand command, StompHeaders headers,
        			byte[] payload, Throwable exception) {
        		// // System.out.println("handleException error:"+ exception.getMessage());
        		// // System.out.println(exception);
        	}
        	@Override
        	public void handleTransportError(StompSession session, Throwable exception) {
        		// // System.out.println("Transport error:"+ exception.getMessage());
        		exception.printStackTrace();
        	    // Handle error, potentially retry or notify user
        	}
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            	session.isConnected();
                session.subscribe("/topic/greetings", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Message.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        Message receivedMessage = (Message) payload;
                        // // System.out.println("receivedMessage: "+ receivedMessage);
                        assertEquals(message.getContent(), receivedMessage.getContent());
                        latch.countDown();
                    }
                });

                // Correct the destination to match the @MessageMapping("/hello") in the controller
                session.send("/app/hello", message);
            }
        };
        String webSocketEndpointUrl = "ws://localhost:" + port + "/gs-guide-websocket";
        webSocketEndpointUrl = "ws://localhost:" + port + "/ws";
        // // System.out.println("url: " + webSocketEndpointUrl);
        stompClient.connect(webSocketEndpointUrl, sessionHandler);

        // Wait for the response for up to 10 seconds
        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new AssertionError("WebSocket message not received");
        }
    }
}
