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
import AppContent from './AppContent.jsx';

function App() {
  const [menuOpen, setMenuOpen] = useState(true);
  const [selectedEvent, setSelectedEvent] = useState(null);

  return (
    <ThemeProvider theme={theme}>
      <AuthProvider> 
        <AppContent />
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;