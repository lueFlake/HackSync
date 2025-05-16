import { styled } from '@mui/material';
import { Alert } from 'antd';
import moment from 'moment';
import React, { useState } from 'react';
import { Navigate, Route, BrowserRouter as Router, Routes, useLocation } from 'react-router-dom';
import Menu from './components/Menu';
import PageContainer from './components/PageContainer';
import TopBar from './components/TopBar';
import { useAuth } from './contexts/AuthContext';
import { useSelectedHackathon } from './hooks/useSelectedEvent';
import BoardPage from './pages/BoardPage.jsx';
import CalendarPage from './pages/CalendarPage';
import ChatPage from './pages/ChatPage';
import EventDetailsPage from './pages/EventDetailsPage';
import EventsPage from './pages/EventsPage';
import LoginPage from './pages/LoginPage';
import MyTasksPage from './pages/MyTasksPage';
import ProfilePage from './pages/ProfilePage';
import TaskPage from './pages/TaskPage';

const AppContainer = styled('div')({
  display: 'flex',
  flexDirection: 'column',
  height: '100vh',
});

const MainContent = styled('main', {
  shouldForwardProp: (prop) => prop !== 'menuOpen',
})(({ theme, menuOpen }) => ({
  flexGrow: 1,
  padding: theme.spacing(3),
  marginLeft: menuOpen ? '240px' : 0,
  transition: theme.transitions.create(['margin', 'width'], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
}));

const PrivateRoute = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
};

const ProtectedContent = ({ children, title }) => {
  const { selectedHackathon, selectedTeamId } = useSelectedHackathon();

  if (!selectedHackathon) {
    return (
      <PageContainer title={title}>
        <Alert
          message="Выберите хакатон"
          description="Для доступа необходимо выбрать хакатон"
          type="info"
          showIcon
        />
      </PageContainer>
    );
  }

  if (!selectedTeamId) {
    return (
      <PageContainer title={`${title} - ${selectedHackathon.name}`}>
        <Alert
          message="Присоединитесь к команде"
          description="Для доступа необходимо присоединиться к команде"
          type="info"
          showIcon
        />
      </PageContainer>
    );
  }

  return (
    <PageContainer title={selectedHackathon ? `${title} - ${selectedHackathon.name}` : title}>
      {children}
    </PageContainer>
  );
};

function AppContent() {
  const [menuOpen, setMenuOpen] = useState(true);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const { isAuthenticated } = useAuth();

  return (
    <Router>
      <AppContainer>
        <TopBar menuOpen={menuOpen} setMenuOpen={setMenuOpen} />
        {isAuthenticated && <Menu open={menuOpen} setOpen={setMenuOpen} />}
        <MainContent menuOpen={menuOpen}>
          <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route path="/" element={
              <PrivateRoute>
                <EventsPage setSelectedEvent={setSelectedEvent} />
              </PrivateRoute>
            } />

            <Route path="/events/:id" element={
              <PrivateRoute>
                <EventDetailsPage />
              </PrivateRoute>
            } />

            <Route path="/board" element={
              <PrivateRoute>
                <ProtectedContent title="Доска">
                  <BoardPage event={selectedEvent} />
                </ProtectedContent>
              </PrivateRoute>
            } />

            <Route path="/calendar/:yearMonth" element={
              <PrivateRoute>
                <ProtectedContent title="Календарь">
                  <CalendarPage />
                </ProtectedContent>
              </PrivateRoute>
            } />

            <Route path="/calendar" element={
              <PrivateRoute>
                <ProtectedContent title="Календарь">
                  <CalendarPage />
                </ProtectedContent>
              </PrivateRoute>
            } />

            <Route path="/tasks" element={
              <PrivateRoute>
                <ProtectedContent title="Мои задачи">
                  <MyTasksPage event={selectedEvent} />
                </ProtectedContent>
              </PrivateRoute>
            } />

            <Route path="/chat" element={
              <PrivateRoute>
                <ProtectedContent title="Чат">
                  <ChatPage event={selectedEvent} />
                </ProtectedContent>
              </PrivateRoute>
            } />

            <Route path="/profile" element={
              <PrivateRoute>
                <ProfilePage />
              </PrivateRoute>
            } />

            <Route path="/tasks/:taskId" element={
              <PrivateRoute>
                <ProtectedContent title="Задача">
                  <TaskPage />
                </ProtectedContent>
              </PrivateRoute>
            } />

            <Route path="*" element={
              <PrivateRoute>
                <Navigate to={`/calendar/${moment().format('YYYY-MM')}`} replace />
              </PrivateRoute>
            } />
          </Routes>
        </MainContent>
      </AppContainer>
    </Router>
  );
}

export default AppContent;