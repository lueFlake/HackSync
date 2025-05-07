import { styled, ThemeProvider } from '@mui/material/styles';
import moment from 'moment';
import React, { useState } from 'react';
import { Navigate, Route, BrowserRouter as Router, Routes, useLocation } from 'react-router-dom';
import Menu from './components/Menu';
import TopBar from './components/TopBar';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import BoardPage from './pages/BoardPage.jsx';
import CalendarPage from './pages/CalendarPage';
import ChatPage from './pages/ChatPage';
import EventsPage from './pages/EventsPage';
import LoginPage from './pages/LoginPage';
import MyTasksPage from './pages/MyTasksPage';
import ProfilePage from './pages/ProfilePage';
import TaskPage from './pages/TaskPage';
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