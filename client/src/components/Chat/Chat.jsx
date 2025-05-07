import { Box, Button, List, ListItem, ListItemText, Paper, TextField, Typography } from '@mui/material';
import { styled } from '@mui/material/styles';
import React, { useEffect, useRef, useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';

const ChatContainer = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(2),
  height: '600px',
  display: 'flex',
  flexDirection: 'column',
}));

const MessageList = styled(List)({
  flex: 1,
  overflow: 'auto',
  marginBottom: '16px',
});

const MessageInput = styled(Box)({
  display: 'flex',
  gap: '8px',
});

const MessageBubble = styled(({ isOwn, ...other }) => <Box {...other} />)(
  ({ theme, isOwn }) => ({
    alignSelf: isOwn ? 'flex-end' : 'flex-start',
    backgroundColor: isOwn ? theme.palette.primary.main : theme.palette.grey[300],
    color: isOwn ? theme.palette.primary.contrastText : theme.palette.text.primary,
    padding: theme.spacing(1.5),
    borderRadius: isOwn
      ? '16px 16px 0 16px'
      : '16px 16px 16px 0',
    maxWidth: '60%',
    marginBottom: theme.spacing(1),
    boxShadow: theme.shadows[1],
    wordBreak: 'break-word',
  })
);


const Chat = () => {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [connected, setConnected] = useState(false);
  const [ws, setWs] = useState(null);
  const messagesEndRef = useRef(null);  
  const { user, isLoading, isAuthenticated } = useAuth();

  // Fetch message history
  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await fetch('http://localhost:8080/chat/history');
        if (response.ok) {
          const history = await response.json();
          setMessages(history);
        }
      } catch (error) {
        console.error('Error fetching chat history:', error);
      }
    };

    fetchHistory();
  }, []);

  useEffect(() => {
    const websocket = new WebSocket('ws://localhost:8080/chat');

    websocket.onopen = () => {
      setConnected(true);
      console.log('Connected to WebSocket');
    };

    websocket.onmessage = (event) => {
      try {
        // First try to parse as JSON
        const message = JSON.parse(event.data);
        setMessages((prev) => [...prev, message]);
      } catch (e) {
        // If not JSON, treat as plain text message
        const textMessage = {
          sender: 'System',
          content: event.data,
          timestamp: Date.now(),
          type: 'TEXT'
        };
        setMessages((prev) => [...prev, textMessage]);
      }
    };

    websocket.onclose = () => {
      setConnected(false);
      console.log('Disconnected from WebSocket');
    };

    websocket.onerror = (error) => {
      setConnected(false);
      console.error('WebSocket error:', error);
    };

    setWs(websocket);

    return () => {
      websocket.close();
    };
  }, []);

  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [messages]);

  const handleSendMessage = () => {
    if (!connected || !newMessage.trim() || !ws) return;
    
    const message = {
      sender: user.name, 
      content: newMessage,
      timestamp: Date.now(),
      type: 'TEXT'
    };

    ws.send(JSON.stringify(message));
    setNewMessage('');
  };
  
  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <div>Please log in</div>;
  }

  return (
    <ChatContainer elevation={3}>
      <Typography variant="h6" gutterBottom>
        Chat
      </Typography>
      <MessageList>
        {messages.map((message, index) => {
          const isOwn = message.sender === user.name; // adjust if needed

          console.log(user)
          return (
            <Box
              key={index}
              display="flex"
              justifyContent={isOwn ? 'flex-end' : 'flex-start'}
            >
              <MessageBubble isOwn={isOwn}>
                <Typography variant="body2">{message.content}</Typography>
                <Typography
                  variant="caption"
                  sx={{
                    display: 'block',
                    textAlign: isOwn ? 'right' : 'left',
                    mt: 0.5,
                  }}
                >
                  {message.sender} — {new Date(message.timestamp).toLocaleTimeString()}
                </Typography>
              </MessageBubble>
            </Box>
          );
        })}
        <div ref={messagesEndRef} />
      </MessageList>
      <MessageInput>
        <TextField
          fullWidth
          variant="outlined"
          placeholder={connected ? "Type a message..." : "Connecting..."}
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          onKeyPress={(e) => {
            if (e.key === 'Enter') {
              handleSendMessage();
            }
          }}
          disabled={!connected}
        />
        <Button
          variant="contained"
          color="primary"
          onClick={handleSendMessage}
          disabled={!newMessage.trim() || !connected}
        >
          Отправить
        </Button>
      </MessageInput>
      {!connected && (
        <Typography variant="caption" color="textSecondary" sx={{ mt: 1 }}>
          Подключение к серверу...
        </Typography>
      )}
    </ChatContainer>
  );
};

export default Chat; 