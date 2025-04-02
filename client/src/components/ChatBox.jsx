// здесь просто Ant описание страницы

import React, { useEffect, useRef } from 'react';
import {
  Box,
  List,
  ListItem,
  Avatar,
  IconButton,
  Typography,
  TextField,
  Button
} from '@mui/material';
import { ExpandMore } from '@mui/icons-material';

const ChatBox = ({
  messages,
  chatHeight,
  newMessage,
  onSendMessage,
  onMessageChange,
  onStartResize,
  onKeyDown
}) => {
  const messagesEndRef = useRef(null);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <>
      <Box
        border="1px solid #ddd"
        p={2}
        borderRadius={2}
        mb={2}
        height={`${chatHeight}px`}
        overflow="auto"
        bgcolor="#f9f9f9"
      >
        <List>
          {messages.map((message) => (
            <ListItem
              key={message.id}
              alignItems="flex-start"
              sx={{
                justifyContent: message.sender === 'me' ? 'flex-end' : 'flex-start',
                py: 1,
                wordBreak: 'break-word'
              }}
            >
              <Box
                sx={{
                  display: 'flex',
                  flexDirection: message.sender === 'me' ? 'row-reverse' : 'row',
                  alignItems: 'flex-start',
                  maxWidth: '80%'
                }}
              >
                {message.sender !== 'me' && (
                  <Avatar sx={{ mr: 1, bgcolor: 'primary.main' }}>
                    {message.name?.[0]}
                  </Avatar>
                )}

                <Box
                  sx={{
                    bgcolor: message.sender === 'me' ? 'primary.main' : 'background.paper',
                    color: message.sender === 'me' ? 'primary.contrastText' : 'text.primary',
                    p: 2,
                    borderRadius: 2,
                    boxShadow: 1,
                    whiteSpace: 'pre-wrap'
                  }}
                >
                  {message.sender !== 'me' && (
                    <Typography fontWeight="bold" gutterBottom>
                      {message.name}
                    </Typography>
                  )}
                  <Typography component="div" sx={{ whiteSpace: 'pre-wrap' }}>
                    {message.text}
                  </Typography>
                  <Typography
                    variant="caption"
                    display="block"
                    textAlign="right"
                    color={message.sender === 'me' ? 'text.light' : 'text.secondary'}
                  >
                    {message.timestamp}
                  </Typography>
                </Box>
              </Box>
            </ListItem>
          ))}
          <div ref={messagesEndRef} />
        </List>
      </Box>

      <IconButton
        onMouseDown={onStartResize}
        sx={{
          width: '100%',
          height: '20px',
          bgcolor: '#ddd',
          cursor: 'ns-resize',
          borderRadius: 0,
          '&:hover': { bgcolor: '#ccc' }
        }}
      >
        <ExpandMore />
      </IconButton>

      <Box display="flex" alignItems="flex-end" gap={2}>
        <TextField
          label="Новое сообщение"
          variant="outlined"
          fullWidth
          multiline
          rows={4}
          value={newMessage}
          onChange={onMessageChange}
          onKeyDown={onKeyDown}
          margin="normal"
        />
        <Button
          variant="contained"
          onClick={onSendMessage}
          disabled={!newMessage.trim()}
          sx={{ height: '56px', minWidth: '120px' }}
        >
          Отправить
        </Button>
      </Box>
    </>
  );
};

export default ChatBox;