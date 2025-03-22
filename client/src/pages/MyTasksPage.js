import React from 'react';
import { Typography, Box, List, ListItem, ListItemText } from '@mui/material';
import PageContainer from '../components/PageContainer';

const MyTasksPage = ({ event }) => {
  return (
    <PageContainer>
      <Box p={3}>
      <Typography variant="h4" gutterBottom>
          Мои задачи: {event?.name || 'Событие не выбрано'}
      </Typography>
      <List>
          <ListItem>
          <ListItemText primary="Пример задачи 1" secondary="Статус: В работе" />
          </ListItem>
          <ListItem>
          <ListItemText primary="Пример задачи 2" secondary="Статус: На проверке" />
          </ListItem>
      </List>
      </Box>
    </PageContainer>
  );
};

export default MyTasksPage;