import React, { useState } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Menu,
  MenuItem,
  Box
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import AccountCircle from '@mui/icons-material/AccountCircle';
import { Divider } from 'antd';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';

const TopBar = ({ menuOpen, setMenuOpen }) => {
  const [anchorEl, setAnchorEl] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(AuthService.isAuthenticated());
  const navigate = useNavigate();

  const handleMenuToggle = () => {
    setMenuOpen(!menuOpen);
  };

  const updateAuthStatus = () => {
    setIsAuthenticated(AuthService.isAuthenticated());
  };

  const handleProfileClick = () => {
    if (isAuthenticated) {
      navigate('/profile');
    } else {
      navigate('/login', { state: { from: '/profile' } });
    }
  };

  const handleLogout = () => {
    AuthService.logout();
    updateAuthStatus();
    navigate('/login');
  };

  return (
    <AppBar
      position="fixed"
      sx={{
        backgroundColor: '#414141',
        zIndex: (theme) => theme.zIndex.drawer + 1,
        height: 64,
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0
      }}
    >
      <Toolbar>
        {/* Кнопка бургер-меню */}
        <IconButton
          color="inherit"
          edge="start"
          onClick={handleMenuToggle}
          sx={{ mr: 2 }}
        >
          <MenuIcon />
        </IconButton>

        {/* Логотип и название */}
        <Box sx={{ display: 'flex', alignItems: 'center', flexGrow: 1 }}>
          <Box
            component="img"
            src="/icons/HackSync.png"
            alt="Лого"
            sx={{
              height: 60,
              width: 60,
              mr: 2
            }}
          />
          <Typography
            variant="h5"
            sx={{
              fontWeight: 'bold',
              color: 'white',
              textTransform: 'uppercase'
            }}
          >
            HackSync
          </Typography>
        </Box>

        {/* Иконка пользователя */}
        <IconButton
          color="inherit"
          onClick={(e) => setAnchorEl(e.currentTarget)}
        >
          <AccountCircle fontSize="large" />
        </IconButton>

        {/* Выпадающее меню пользователя */}
        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={() => setAnchorEl(null)}
        >
          {/* !!!если не авторизованным пользователям можно только на авторизацию то это надо менять!!! */}
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