import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { styled } from '@mui/material/styles';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import Menu from './components/Menu';
import TopBar from './components/TopBar';
import EventsPage from './pages/EventsPage';
import BoardPage from './pages/BoardPage';
import CalendarPage from './pages/CalendarPage';
import MyTasksPage from './pages/MyTasksPage';
import ChatPage from './pages/ChatPage';
import moment from 'moment';
const theme = createTheme();

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

function App() {
  const [menuOpen, setMenuOpen] = useState(true);
  const [selectedEvent, setSelectedEvent] = useState(null);

  return (
    <ThemeProvider theme={theme}>
      <Router>
        <AppContainer>
          <TopBar menuOpen={menuOpen} setMenuOpen={setMenuOpen} />
          <Menu open={menuOpen} setOpen={setMenuOpen} />
          <MainContent menuOpen={menuOpen}>
            <Routes>
              <Route path="/" element={<EventsPage setSelectedEvent={setSelectedEvent} />} />
              <Route path="/board" element={<BoardPage event={selectedEvent} />} />
              <Route path="/calendar/:yearMonth" element={<CalendarPage />} />
              <Route path="/calendar" element={<CalendarPage />} />
              <Route path="*" element={<Navigate to={`/calendar/${moment().format('YYYY-MM')}`} replace />} />
              <Route path="/tasks" element={<MyTasksPage event={selectedEvent} />} />
              <Route path="/chat" element={<ChatPage event={selectedEvent} />} />
            </Routes>
          </MainContent>
        </AppContainer>
      </Router>
    </ThemeProvider>
  );
}

export default App;