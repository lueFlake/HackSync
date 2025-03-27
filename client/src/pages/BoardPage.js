import React from 'react';
import { Typography, Box } from '@mui/material';
import PageContainer from '../components/PageContainer';
import KanbanBoard from '../components/KanbanBoard';

const BoardPage = ({ event }) => {
  return (
    <PageContainer title={`Доска задач: ${event?.name || 'Событие не выбрано'}`}>
      <KanbanBoard />
    </PageContainer>
  );
};

export default BoardPage;