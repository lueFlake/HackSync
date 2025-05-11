import { Box } from '@mui/material';
import React from 'react';
import Chat from '../components/Chat/Chat';
import PageContainer from '../components/PageContainer';
import { useSelectedHackathon } from '../hooks/useSelectedHackathon';

const ChatPage = () => {
  const { selectedHackathon } = useSelectedHackathon();
  
  return (
    <PageContainer title={selectedHackathon ? `Чат - ${selectedHackathon.name}` : "Чат"}>
      <Box p={3}>
        <Chat />
      </Box>
    </PageContainer>
  );
};

export default ChatPage;