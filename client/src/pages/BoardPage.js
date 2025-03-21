import React from 'react';
import { Typography, Box } from '@mui/material';
import PageContainer from '../components/PageContainer';
import KanbanBoard from '../components/KanbanBoard';

const BoardPage = ({ event }) => {
  return (
    <PageContainer>
      <Typography variant="h4" gutterBottom>
          Доска задач: {event?.name || 'Событие не выбрано'}
      </Typography>
      <KanbanBoard />
    </PageContainer>
  );
};

export default BoardPage;