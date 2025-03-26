import React, { useState, useRef, useEffect } from 'react';
import {
  Typography,
  Box,
  TextField,
  Button,
  List,
  ListItem,
  Avatar,
  IconButton
} from '@mui/material';
import { ExpandMore, ExpandLess } from '@mui/icons-material';
import PageContainer from '../components/PageContainer';

const ChatPage = ({ event }) => {
  /**
   * Состояния компонента:
   */
  const [messages, setMessages] = useState([
    {
      id: 1,
      text: "Привет! Это тестовое сообщение для демонстрации работы чата.",
      timestamp: "12:30",
      sender: "other",
      name: "Алексей"
    },
    {
      id: 2,
      text: "Проверка длинного сообщения с автоматическим переносом строки.",
      timestamp: "12:31",
      sender: "me"
    }
  ]); // Список всех сообщений в чате

  const [newMessage, setNewMessage] = useState(''); // Текст нового сообщения
  const [isSending, setIsSending] = useState(false); // Флаг процесса отправки (для отключения кнопки)
  const [chatHeight, setChatHeight] = useState(300); // Высота контейнера с сообщениями (изменяемая)
  const chatRef = useRef(null); // Реф для доступа к DOM-элементу контейнера с сообщениями

  /**
   * Обработчик отправки сообщения
   * - Проверяет, что поле ввода не пустое.
   * - Добавляет новое сообщение в список с уникальным ID, текстом и временем отправки.
   * - Очищает поле ввода и прокручивает страницу вниз.
   */
  const handleSendMessage = () => {
    if (!newMessage.trim()) return; // Если поле пустое, ничего не делаем

    setIsSending(true);

    // ?? Тут должно быть взаимодействие с беком, но пока просто имитация отправки сообщения ??
    setTimeout(() => {
      setMessages([
        ...messages,
        {
          id: Date.now(),
          text: newMessage,
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          sender: 'me'
        }
      ]);

      setNewMessage('');
      setIsSending(false);

      scrollToBottom();
    }, 100); // Имитация задержки отправки
  };

  /**
   * Прокрутка страницы вниз
   * - Автоматически прокручивает контейнер до последнего сообщения.
   */
  const scrollToBottom = () => {
    if (chatRef.current) {
      chatRef.current.scrollTop = chatRef.current.scrollHeight;
    }
  };

  /**
   * Обработчик нажатия Enter
   * - Предотвращает добавление новой строки при нажатии Enter.
   * - Вызывает обработчик отправки сообщения.
   */
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  /**
   * Логика растягивания контейнера мышкой
   */
  const [isResizing, setIsResizing] = useState(false);
  const resizeStartY = useRef(null); // Начальная позиция курсора при начале изменения размера
  const initialHeight = useRef(null); // Начальная высота контейнера

  /**
   * Начало изменения размера
   * - Сохраняет начальную позицию курсора и высоту контейнера.
   * - Активирует флаг изменения размера.
   */
  const startResize = (e) => {
    setIsResizing(true); // Включаем флаг изменения размера
    resizeStartY.current = e.clientY; // Сохраняем начальную позицию курсора
    initialHeight.current = chatHeight; // Сохраняем начальную высоту контейнера
  };

  /**
   * Изменение размера контейнера
   * - Вычисляет новую высоту на основе движения курсора.
   * - Устанавливает минимальную высоту в 100px.
   */
  const resize = (e) => {
    if (isResizing) {
      const deltaY = resizeStartY.current - e.clientY; // Разница между текущей и начальной позицией курсора
      const newHeight = Math.max(100, initialHeight.current - deltaY); // Вычисляем новую высоту (минимум 100px)
      setChatHeight(newHeight);
    }
  };

  /**
   * Завершение изменения размера
   * - Деактивирует флаг изменения размера.
   */
  const stopResize = () => {
    setIsResizing(false);
  };

  /**
   * Эффект для удаления обработчиков событий после завершения изменения размера
   * - Добавляет обработчики событий `mousemove` и `mouseup` при начале изменения размера.
   * - Удаляет обработчики событий после завершения изменения размера.
   */
  useEffect(() => {
    if (isResizing) {
      window.addEventListener('mousemove', resize); // Добавляем обработчик движения мыши
      window.addEventListener('mouseup', stopResize); // Добавляем обработчик отпускания кнопки мыши
    }

    return () => {
      window.removeEventListener('mousemove', resize); // Удаляем обработчик движения мыши
      window.removeEventListener('mouseup', stopResize); // Удаляем обработчик отпускания кнопки мыши
    };
  }, [isResizing]);

  return (
    <PageContainer>
      <Box p={3}>
        {/* Заголовок чата */}
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h4" gutterBottom>
            Чат: {event?.name || 'Событие не выбрано'}
          </Typography>
        </Box>

        {/* Контейнер сообщений */}
        <Box
          ref={chatRef} // Реф для доступа к DOM-элементу
          id="messages-container"
          border="1px solid #ddd"
          p={2} // Отступ внутри контейнера
          borderRadius={2}
          mb={2}
          height={`${chatHeight}px`} // Динамическая высота контейнера
          overflow="auto"
          bgcolor="#f9f9f9"
        >
          {/* Список сообщений */}
          <List>
            {messages.map((message) => (
              <ListItem
                key={message.id}
                alignItems="flex-start"
                sx={{
                  justifyContent: message.sender === 'me' ? 'flex-end' : 'flex-start', // Выравнивание сообщений
                  py: 1, // Отступ сверху и снизу
                  wordBreak: 'break-word'
                }}
              >
                <Box
                  sx={{
                    display: 'flex', // Гибкий контейнер
                    flexDirection: message.sender === 'me' ? 'row-reverse' : 'row',
                    alignItems: 'flex-start',
                    maxWidth: '80%'
                  }}
                >
                  {/* Аватар отправителя (если сообщение не от текущего пользователя) */}
                  {message.sender !== 'me' && (
                    <Avatar sx={{ mr: 1, bgcolor: 'primary.main' }}>
                      {message.name?.[0]} {/* Первая буква имени отправителя */}
                    </Avatar>
                  )}

                  {/* Тело сообщения */}
                  <Box
                    sx={{
                      bgcolor: message.sender === 'me' ? 'primary.main' : 'background.paper',
                      color: message.sender === 'me' ? 'primary.contrastText' : 'text.primary',
                      p: 2, // Отступы внутри
                      borderRadius: 2,
                      whiteSpace: 'pre-wrap',
                      boxShadow: 1,
                      maxWidth: '100%',
                      wordWrap: 'break-word'
                    }}
                  >
                    {/* Имя отправителя (если сообщение не от текущего пользователя) */}
                    {message.sender !== 'me' && (
                      <Typography fontWeight="bold" gutterBottom>
                        {message.name}
                      </Typography>
                    )}
                    {/* Текст сообщения */}
                    <Typography>{message.text}</Typography>
                    {/* Время отправки */}
                    <Typography
                      variant="caption"
                      display="block"
                      textAlign="right"
                      color={message.sender === 'me' ? 'text.light' : 'text.secondary'}
                    >
                      {message.timestamp}
                    </Typography>
                  </Box>
                </Box>
              </ListItem>
            ))}
          </List>
        </Box>

        {/* Видимая кнопка для изменения размера */}
        <IconButton
          onMouseDown={startResize}
          sx={{
            width: '100%',
            height: '20px',
            bgcolor: '#ddd',
            cursor: 'ns-resize',
            borderRadius: 0,
            '&:hover': {
              bgcolor: '#ccc' // Цвет при наведении
            }
          }}
        >
          <ExpandMore /> {/* Иконка стрелки вниз */}
        </IconButton>

        {/* Блок ввода сообщения */}
        <Box display="flex" alignItems="flex-end" gap={2}>
          {/* Поле ввода сообщения */}
          <TextField
            label="Новое сообщение"
            variant="outlined"
            fullWidth
            multiline
            rows={4}
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            onKeyPress={handleKeyPress}
            disabled={isSending}
            margin="normal"
            inputProps={{
              maxLength: 5000 // ?? Максимальная длина сообщения (не уверен нужна ли вообще и если нужна то сколько) ??
            }}
          />
          {/* Кнопка отправки */}
          <Button
            variant="contained"
            onClick={handleSendMessage}
            disabled={!newMessage.trim() || isSending} // Отключение кнопки при пустом поле или отправке
            sx={{ height: '56px', minWidth: '120px' }}
          >
            {isSending ? 'Отправка...' : 'Отправить'}
          </Button>
        </Box>
      </Box>
    </PageContainer>
  );
};

export default ChatPage;