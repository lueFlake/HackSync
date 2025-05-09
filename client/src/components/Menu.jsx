import React from 'react';
import { Drawer, List, ListItem, ListItemIcon, ListItemText } from '@mui/material';
import {
  Event as EventIcon,
  Dashboard as DashboardIcon,
  CalendarToday as CalendarIcon,
  Task as TaskIcon,
  Chat as ChatIcon
} from '@mui/icons-material';

const Menu = ({ open, setOpen }) => {
  return (
      <Drawer
        sx={{
          width: 240,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: 240,
            boxSizing: 'border-box',
            marginTop: '64px'
          },
        }}
        variant="persistent"
        anchor="left"
        open={open}
      >
        {/* ичпользование navigate или <Link> должно быть лучше */}
        <List>
          {[
            { text: 'События', icon: <EventIcon />, path: '/' },
            { text: 'Доска', icon: <DashboardIcon />, path: '/board' },
            { text: 'Календарь', icon: <CalendarIcon />, path: '/calendar' },
            { text: 'Мои задачи', icon: <TaskIcon />, path: '/tasks' },
            { text: 'Чат', icon: <ChatIcon />, path: '/chat' },
          ].map((item) => (
            <ListItem button key={item.text} component="a" href={item.path}>
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItem>
          ))}
        </List>
      </Drawer>
  );
};

export default Menu;