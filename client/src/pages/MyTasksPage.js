import React from 'react';
import { Typography } from 'antd';
import PageContainer from '../components/PageContainer';
import MyTasksTable from '../components/MyTasksTable';

const { Text } = Typography;

const MyTasksPage = ({ event }) => {
  // Статичные данные задач
  const tasks = [
    {
      id: 1,
      name: 'Реализовать авторизацию',
      description: 'Добавить JWT-аутентификацию на бэкенде',
      status: 'in_progress',
      priority: 'high',
      deadline: '2023-12-15'
    },
    {
      id: 2,
      name: 'Написать тесты для API',
      description: 'Покрыть модульными тестами основные endpoints',
      status: 'review',
      priority: 'medium',
      deadline: '2023-12-20'
    },
    {
      id: 3,
      name: 'Исправить баг с роутингом',
      description: 'При перезагрузке страницы теряется текущий route',
      status: 'todo',
      priority: 'critical',
      deadline: '2023-12-10'
    }
  ];

  return (
    <PageContainer>
      <Text strong style={{ fontSize: 24, display: 'block', marginBottom: 16 }}>
        Мои задачи: {event?.name || 'Текущий проект'}
      </Text>

      <MyTasksTable tasks={tasks} />
    </PageContainer>
  );
};

export default MyTasksPage;