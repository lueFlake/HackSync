# WebSocket Chat API Documentation

## Overview
The WebSocket Chat API provides real-time messaging functionality for the application. It uses the WebSocket protocol for bi-directional communication between clients and the server.

## Connection Details

### Endpoint
```
ws://{host}/chat
```

### Connection Parameters
- Protocol: WebSocket (ws:// or wss:// for secure connections)
- Path: /chat
- No authentication required (can be added if needed)

## Message Format

### Chat Message Structure
```json
{
  "sender": "string",    // Name or identifier of the message sender
  "content": "string",   // The message content
  "timestamp": number    // Unix timestamp in milliseconds
}
```

### Example Message
```json
{
  "sender": "John",
  "content": "Hello, everyone!",
  "timestamp": 1647123456789
}
```

## Connection Flow

1. **Establish Connection**
   - Connect to the WebSocket endpoint
   - No handshake parameters required

2. **Connection Confirmation**
   - Server sends a welcome message
   - Format: "You are connected! There are X users here."

3. **Sending Messages**
   - Send JSON-formatted messages
   - Messages are broadcast to all connected clients
   - Invalid messages will receive an error response

4. **Receiving Messages**
   - Messages are received as JSON strings
   - Parse using the ChatMessage structure
   - Messages are received in real-time

5. **Error Handling**
   - Invalid JSON: "Error: Invalid message format"
   - Connection errors are logged server-side

## Features

- **Real-time Broadcasting**: All connected clients receive messages immediately
- **Automatic Connection Management**: Server tracks connected clients
- **Error Handling**: Invalid messages are rejected with error messages
- **Connection Status**: Clients receive connection confirmation
- **Ping/Pong**: 15-second ping interval to maintain connections

## Best Practices

1. **Connection Management**
   - Implement reconnection logic
   - Handle connection errors gracefully
   - Monitor connection status

2. **Message Handling**
   - Validate message format before sending
   - Handle message parsing errors
   - Implement message queuing for offline scenarios

3. **Security**
   - Use WSS (WebSocket Secure) in production
   - Implement authentication if needed
   - Validate message content

## Example Usage

### JavaScript Client Example
```javascript
const ws = new WebSocket('ws://localhost:8080/chat');

ws.onopen = () => {
    console.log('Connected to chat');
};

ws.onmessage = (event) => {
    const message = JSON.parse(event.data);
    console.log(`Message from ${message.sender}: ${message.content}`);
};

ws.onerror = (error) => {
    console.error('WebSocket error:', error);
};

// Sending a message
const sendMessage = (content) => {
    const message = {
        sender: 'User',
        content: content,
        timestamp: Date.now()
    };
    ws.send(JSON.stringify(message));
};
```

### Kotlin Client Example
```kotlin
val client = HttpClient {
    install(WebSockets)
}

client.webSocket("/chat") {
    // Send message
    val message = ChatMessage("User", "Hello!")
    send(Json.encodeToString(ChatMessage.serializer(), message))
    
    // Receive messages
    for (frame in incoming) {
        when (frame) {
            is Frame.Text -> {
                val receivedMessage = Json.decodeFromString<ChatMessage>(frame.readText())
                println("Received: ${receivedMessage.content}")
            }
        }
    }
}
```

## Limitations

- No message persistence (messages are not stored)
- No private messaging (all messages are broadcast)
- No message history (new clients don't receive previous messages)
- No typing indicators
- No read receipts

## Future Enhancements

1. Message persistence
2. Private messaging
3. Message history
4. Typing indicators
5. Read receipts
6. User presence
7. Message delivery status 