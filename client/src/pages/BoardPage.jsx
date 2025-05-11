import React from 'react';
import KanbanBoard from '../components/KanbanBoard';
import PageContainer from '../components/PageContainer';
import { useSelectedHackathon } from '../hooks/useSelectedHackathon';

const BoardPage = () => {
  const { selectedHackathon } = useSelectedHackathon();
  
  return (
    <PageContainer title={selectedHackathon ? `Доска - ${selectedHackathon.name}` : "Доска"}>
      <KanbanBoard />
    </PageContainer>
  );
};

export default BoardPage;