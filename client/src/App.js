import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { styled } from '@mui/material/styles';
import { ThemeProvider } from '@mui/material/styles';
import Menu from './components/Menu';
import TopBar from './components/TopBar';
import EventsPage from './pages/EventsPage';
import BoardPage from './pages/BoardPage.jsx';
import CalendarPage from './pages/CalendarPage';
import MyTasksPage from './pages/MyTasksPage';
import TaskPage from './pages/TaskPage';
import ChatPage from './pages/ChatPage';
import LoginPage from './pages/LoginPage';
import ProfilePage from './pages/ProfilePage';
import AuthService from './services/AuthService';
import { AuthProvider } from './contexts/AuthContext';
import moment from 'moment';
import theme from './theme';

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
  const isAuthenticated = AuthService.isAuthenticated();
  return isAuthenticated ? children : <Navigate to="/login" replace />;
};

function App() {
  const [menuOpen, setMenuOpen] = useState(true);
  const [selectedEvent, setSelectedEvent] = useState(null);

  return (
    <ThemeProvider theme={theme}>
      <AuthProvider>
        <Router>
          <AppContainer>
            <TopBar menuOpen={menuOpen} setMenuOpen={setMenuOpen} />
            <Menu open={menuOpen} setOpen={setMenuOpen} />
            <MainContent menuOpen={menuOpen}>
              <Routes>
                <Route path="/login" element={<LoginPage />} />

                <Route path="/" element={
                  <PrivateRoute>
                    <EventsPage setSelectedEvent={setSelectedEvent} />
                  </PrivateRoute>
                } />

                <Route path="/board" element={
                  <PrivateRoute>
                    <BoardPage event={selectedEvent} />
                  </PrivateRoute>
                } />

                <Route path="/calendar/:yearMonth" element={
                  <PrivateRoute>
                    <CalendarPage />
                  </PrivateRoute>
                } />

                <Route path="/calendar" element={
                  <PrivateRoute>
                    <CalendarPage />
                  </PrivateRoute>
                } />

                <Route path="/tasks" element={
                  <PrivateRoute>
                    <MyTasksPage event={selectedEvent} />
                  </PrivateRoute>
                } />

                <Route path="/chat" element={
                  <PrivateRoute>
                    <ChatPage event={selectedEvent} />
                  </PrivateRoute>
                } />

                <Route path="/profile" element={
                  <PrivateRoute>
                    <ProfilePage />
                  </PrivateRoute>
                } />

                <Route path="/tasks/:taskId" element={
                  <PrivateRoute>
                    <TaskPage />
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
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;