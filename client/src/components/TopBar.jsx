import AccountCircle from '@mui/icons-material/AccountCircle';
import MenuIcon from '@mui/icons-material/Menu';
import {
  AppBar,
  Box,
  IconButton,
  Menu,
  MenuItem,
  Toolbar,
  Typography
} from '@mui/material';
import { Divider } from 'antd';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext'; // Используем контекст вместо AuthService

const TopBar = ({ menuOpen, setMenuOpen }) => {
  const [anchorEl, setAnchorEl] = useState(null);
  const { isAuthenticated, logout, checkAuth } = useAuth(); // Получаем из контекста
  const navigate = useNavigate();

  // Проверяем аутентификацию при монтировании и при изменении
  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  const handleMenuToggle = () => {
    setMenuOpen(!menuOpen);
  };

  const handleProfileClick = () => {
    setAnchorEl(null); // Закрываем меню
    if (isAuthenticated) {
      navigate('/profile');
    } else {
      navigate('/login', { state: { from: '/profile' } });
    }
  };

  const handleLogout = async () => {
    setAnchorEl(null); // Закрываем меню
    try {
      await logout(); // Используем метод из контекста
      navigate('/login', { state: { from: 'logout' } });
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return (
    <AppBar
      position="fixed"
      sx={{
        backgroundColor: '#414141',
        zIndex: (theme) => theme.zIndex.drawer + 1,
        height: 64,
        top: 0,
        left: 0,
        right: 0
      }}
    >
      <Toolbar>
        <IconButton
          color="inherit"
          edge="start"
          onClick={handleMenuToggle}
          sx={{ mr: 2 }}
          aria-label="menu"
        >
          <MenuIcon />
        </IconButton>

        <Box sx={{ display: 'flex', alignItems: 'center', flexGrow: 1 }}>
          <Box
            component="img"
            src="/icons/HackSync.png"
            alt="Логотип HackSync"
            sx={{
              height: 60,
              width: 60,
              mr: 2,
              cursor: 'pointer'
            }}
            onClick={() => navigate('/')}
          />
          <Typography
            variant="h5"
            sx={{
              fontWeight: 'bold',
              color: 'white',
              textTransform: 'uppercase',
              cursor: 'pointer'
            }}
            onClick={() => navigate('/')}
          >
            HackSync
          </Typography>
        </Box>

        <IconButton
          color="inherit"
          onClick={(e) => setAnchorEl(e.currentTarget)}
          aria-label="account"
        >
          <AccountCircle fontSize="large" />
        </IconButton>

        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={() => setAnchorEl(null)}
          anchorOrigin={{
            vertical: 'bottom',
            horizontal: 'right',
          }}
          transformOrigin={{
            vertical: 'top',
            horizontal: 'right',
          }}
        >
          <MenuItem onClick={handleProfileClick}>
            {isAuthenticated ? 'Профиль' : 'Войти'}
          </MenuItem>
          {isAuthenticated && (
            <>
              <Divider style={{ margin: '8px 0' }} />
              <MenuItem onClick={handleLogout}>Выйти</MenuItem>
            </>
          )}
        </Menu>
      </Toolbar>
    </AppBar>
  );
};

export default TopBar;