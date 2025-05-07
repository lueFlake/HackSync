import { Box } from '@mui/material';
import React from 'react';
import Chat from '../components/Chat/Chat';
import PageContainer from '../components/PageContainer';

const ChatPage = ({ event }) => {
  return (
    <PageContainer title={`Чат: ${event?.name || 'Событие не выбрано'}`}>
      <Box p={3}>
        <Chat />
      </Box>
    </PageContainer>
  );
};

export default ChatPage;