import React, { useState, useRef, useEffect } from 'react';
import PageContainer from '../components/PageContainer';
import ChatBox from '../components/ChatBox';
import { Box } from '@mui/material';

const ChatPage = ({ event }) => {
  const [messages, setMessages] = useState([
    {
      id: 1,
      text: "Привет! Это тестовое сообщение для демонстрации работы чата.",
      timestamp: "12:30",
      sender: "other",
      name: "Алексей"
    },
    {
      id: 2,
      text: "Проверка длинного сообщения с переносом строки:\nЭто новая строка\nИ еще одна",
      timestamp: "12:31",
      sender: "me"
    }
  ]);

  const [newMessage, setNewMessage] = useState('');
  const [isSending, setIsSending] = useState(false);
  const [chatHeight, setChatHeight] = useState(300);
  const [isResizing, setIsResizing] = useState(false);
  const resizeStartY = useRef(null);
  const initialHeight = useRef(null);

  const handleSendMessage = () => {
    if (!newMessage.trim()) return;

    setIsSending(true);

    // надо отправить api запрос вместо этого
    setTimeout(() => {
      setMessages([
        ...messages,
        {
          id: Date.now(),
          text: newMessage,
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          sender: 'me'
        }
      ]);
      setNewMessage('');
      setIsSending(false);
    }, 100);
  };

  const handleMessageChange = (e) => {
    setNewMessage(e.target.value);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const startResize = (e) => {
    setIsResizing(true);
    resizeStartY.current = e.clientY;
    initialHeight.current = chatHeight;
  };

  const resize = (e) => {
    if (isResizing) {
      const deltaY = resizeStartY.current - e.clientY;
      const newHeight = Math.max(100, initialHeight.current - deltaY);
      setChatHeight(newHeight);
    }
  };

  const stopResize = () => {
    setIsResizing(false);
  };

  useEffect(() => {
    if (isResizing) {
      window.addEventListener('mousemove', resize);
      window.addEventListener('mouseup', stopResize);
    }

    return () => {
      window.removeEventListener('mousemove', resize);
      window.removeEventListener('mouseup', stopResize);
    };
  }, [isResizing]);

  return (
    <PageContainer title={`Чат: ${event?.name || 'Событие не выбрано'}`}>
      <Box p={3}>
        <ChatBox
          messages={messages}
          chatHeight={chatHeight}
          newMessage={newMessage}
          onSendMessage={handleSendMessage}
          onMessageChange={handleMessageChange}
          onStartResize={startResize}
          onKeyDown={handleKeyDown}
        />
      </Box>
    </PageContainer>
  );
};

export default ChatPage;