import React from 'react';
import { Typography, Box, TextField, Button } from '@mui/material';
import PageContainer from '../components/PageContainer';

const ChatPage = ({ event }) => {
	return (
		<PageContainer>
			<Box p={3}>
				<Typography variant="h4" gutterBottom>
					Чат: {event?.name || 'Событие не выбрано'}
				</Typography>
				<Box border="1px solid #ddd" p={2} borderRadius={2} mb={2} height="300px">
					{/* Место для сообщений */}
				</Box>
				<TextField
					label="Новое сообщение"
					variant="outlined"
					fullWidth
					margin="normal"
				/>
				<Button variant="contained" sx={{ mt: 1 }}>
					Отправить
				</Button>
			</Box>
		</PageContainer>
	);
};

export default ChatPage;