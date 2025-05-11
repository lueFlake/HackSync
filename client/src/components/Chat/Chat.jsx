import {
  FileExcelOutlined, // Text
  FileImageOutlined, // Archives
  FileOutlined,
  FilePdfOutlined, // Images
  FilePptOutlined, // Excel
  FileTextOutlined, // PDF
  FileWordOutlined,
  PaperClipOutlined,
  SendOutlined
} from "@ant-design/icons";
import { Box, TextField, Typography } from "@mui/material";
import { styled } from "@mui/material/styles";
import { Button } from "antd";
import React, { useEffect, useRef, useState } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { useApi } from "../../hooks/useApi";
import { useSelectedHackathon } from "../../hooks/useSelectedHackathon";
import { ApiService } from "../../services/ApiService";

const MessageBubble = styled(({ isOwn, ...other }) => <Box {...other} />)(
  ({ theme, isOwn }) => ({
    alignSelf: isOwn ? "flex-end" : "flex-start",
    backgroundColor: isOwn
      ? theme.palette.primary.main
      : theme.palette.grey[300],
    color: isOwn
      ? theme.palette.primary.contrastText
      : theme.palette.text.primary,
    padding: theme.spacing(1.5),
    borderRadius: isOwn ? "16px 16px 0 16px" : "16px 16px 16px 0",
    maxWidth: "60%",
    marginBottom: theme.spacing(1),
    boxShadow: theme.shadows[1],
    wordBreak: "break-word",
  })
);

const FileIcon = ({ mimeType }) => {
  // Subtle, chat-friendly colors
  const colors = {
    text: "var(--text-secondary)", // Gray for text
    primary: "var(--primary)", // Your chat's primary color
    accent: "var(--accent)", // Your chat's accent color
    danger: "var(--danger)", // For important files
  };

  const [type, subtype] = mimeType?.split("/") || [];

  const iconProps = {
    style: {
      fontSize: 18,
      marginRight: 8,
    },
  };

  switch (true) {
    case subtype?.includes("pdf"):
      return (
        <FilePdfOutlined
          {...iconProps}
          style={{ ...iconProps.style, color: "#bb2222" }}
        />
      );

    case subtype?.includes("powerpoint") || subtype?.includes("presentation"):
      return (
        <FilePptOutlined
          {...iconProps}
          style={{ ...iconProps.style, color: "#FF9800" }}
        />
      ); // Soft orange

    case subtype?.includes("excel") || subtype?.includes("sheet"):
      return (
        <FileExcelOutlined
          {...iconProps}
          style={{ ...iconProps.style, color: "#4DBF56" }}
        />
      ); // Soft green

    case subtype?.includes("word") || subtype?.includes("document"):
      return (
        <FileWordOutlined
          {...iconProps}
          style={{ ...iconProps.style, color: "#2222bb" }}
        />
      );

    case type === "image":
      return (
        <FileImageOutlined
          {...iconProps}
          style={{ ...iconProps.style, color: colors.accent }}
        />
      );

    case type === "text":
      return (
        <FileTextOutlined
          {...iconProps}
          style={{ ...iconProps.style, color: colors.text }}
        />
      );

    default:
      return (
        <FileOutlined
          {...iconProps}
          style={{ ...iconProps.style, color: colors.text }}
        />
      );
  }
};

const Chat = () => {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState("");
  const [socket, setSocket] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const [reconnectAttempt, setReconnectAttempt] = useState(0);
  const messageQueue = useRef([]);
  const messagesEndRef = useRef(null);
  const fileInputRef = useRef(null);
  const reconnectTimeoutRef = useRef(null);
  const { user, isLoading, isAuthenticated } = useAuth();
  const { selectedHackathon } = useSelectedHackathon();
  const api = useApi();

  const formatFileSize = (bytes) => {
    if (bytes === 0) return "0 Bytes";
    const k = 1024;
    const sizes = ["Bytes", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + " " + sizes[i];
  };

  const cleanMimeType = (mime) => {
    const typeMap = {
      pdf: "PDF",
      "vnd.openxmlformats-officedocument.wordprocessingml.document": "DOCX",
      msword: "DOC",
      "vnd.openxmlformats-officedocument.spreadsheetml.sheet": "XLSX",
      "vnd.ms-excel": "XLS",
      "vnd.openxmlformats-officedocument.presentationml.presentation": "PPTX",
      "vnd.ms-powerpoint": "PPT",
    };

    const subtype = mime.split("/")[1]?.toLowerCase();
    return typeMap[subtype] || subtype?.toUpperCase() || "FILE";
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const processMessageQueue = () => {
    if (socket && socket.readyState === WebSocket.OPEN) {
      while (messageQueue.current.length > 0) {
        const message = messageQueue.current.shift();
        socket.send(JSON.stringify(message));
      }
    }
  };

  const queueMessage = (message) => {
    messageQueue.current.push(message);
    if (isConnected) {
      processMessageQueue();
    }
  };

  const handleSendMessage = () => {
    if (!newMessage.trim() || !selectedHackathon) return;

    const message = {
      sender: user.name,
      content: newMessage,
      timestamp: Date.now(),
      type: "TEXT",
      hackathonId: selectedHackathon.id
    };

    // Add message to local state immediately
    setMessages(prev => [...prev, message]);
    
    // Send through WebSocket
    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.send(JSON.stringify(message));
    } else {
      queueMessage(message);
    }
    
    setNewMessage('');
  };

  const handleFileUpload = async (event) => {
    if (!selectedHackathon) return;

    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await api.post(`/chat/upload/${selectedHackathon.id}`, formData);

      if (response.data && response.data.url) {
        const message = {
          sender: user.name,
          content: {
            url: response.data.url,
            fileName: response.data.fileName,
            mimeType: file.type,
            fileSize: file.size
          },
          timestamp: Date.now(),
          type: "FILE",
          hackathonId: selectedHackathon.id
        };

        // Add message to local state
        setMessages(prev => [...prev, message]);
        
        // Send through WebSocket
        if (socket && socket.readyState === WebSocket.OPEN) {
          socket.send(JSON.stringify(message));
        } else {
          queueMessage(message);
        }
      } else {
        console.error('Invalid response from file upload');
      }
    } catch (error) {
      console.error('Failed to upload file:', error);
    }
  };

  const connectWebSocket = () => {
    if (!selectedHackathon) return;

    try {
      console.log(`Attempting to connect (attempt ${reconnectAttempt + 1})`);
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const wsUrl = `${protocol}//localhost:8080/chat/${selectedHackathon.id}`;
      console.log('Connecting to WebSocket URL:', wsUrl);
      
      const ws = new WebSocket(wsUrl);
      
      ws.onopen = () => {
        console.log('WebSocket connection established successfully');
        setIsConnected(true);
        setReconnectAttempt(0);
        processMessageQueue();
      };

      ws.onmessage = (event) => {
        try {
          console.log('Received message:', event.data);
          const message = JSON.parse(event.data);
          // Only add message if it's from another user
          if (message.sender !== user.name) {
          setMessages(prev => [...prev, message]);
          }
        } catch (error) {
          console.error('Failed to parse message:', error);
        }
      };

      ws.onerror = (error) => {
        console.error('WebSocket error:', error);
        setIsConnected(false);
        ws.close();
      };

      ws.onclose = (event) => {
        console.log('WebSocket connection closed:', {
          code: event.code,
          reason: event.reason,
          wasClean: event.wasClean
        });
        setIsConnected(false);
        if (event.code !== 1000) { // Don't reconnect if closed normally
          scheduleReconnect();
        }
      };

      setSocket(ws);
    } catch (error) {
      console.error('Failed to create WebSocket:', error);
      scheduleReconnect();
    }
  };

  const scheduleReconnect = () => {
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
    }

    const maxAttempts = 5;
    const baseDelay = 1000; // 1 second
    const maxDelay = 30000; // 30 seconds

    if (reconnectAttempt < maxAttempts) {
      const delay = Math.min(baseDelay * Math.pow(2, reconnectAttempt), maxDelay);
      console.log(`Scheduling reconnect in ${delay}ms (attempt ${reconnectAttempt + 1})`);
      reconnectTimeoutRef.current = setTimeout(() => {
        setReconnectAttempt(prev => prev + 1);
        connectWebSocket();
      }, delay);
    }
  };

  useEffect(() => {
    if (!selectedHackathon) {
      setMessages([]);
      setIsConnected(false);
      setReconnectAttempt(0);
      if (socket) {
        socket.close(1000, "Hackathon changed");
        setSocket(null);
      }
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current);
      }
      return;
    }

    // Load message history
    const loadHistory = async () => {
      try {
        const response = await api.get(`/chat/history/${selectedHackathon.id}`);
        setMessages(response.data);
      } catch (error) {
        console.error('Failed to load chat history:', error);
      }
    };

    loadHistory();
    connectWebSocket();

    return () => {
      if (socket) {
        socket.close(1000, "Component unmounted");
      }
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current);
      }
    };
  }, [selectedHackathon]);

  if (isLoading) return <div>Loading...</div>;
  if (!isAuthenticated) return <div>Please log in</div>;

  if (!selectedHackathon) {
    return (
      <Box sx={{ textAlign: 'center', mt: 4 }}>
        <Typography variant="h6" color="text.secondary">
          Выберите хакатон для просмотра чата
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ height: 'calc(100vh - 200px)', display: 'flex', flexDirection: 'column' }}>
      {!isConnected && selectedHackathon && reconnectAttempt < 5 && (
        <Box sx={{ p: 1, bgcolor: 'warning.light', color: 'warning.contrastText', textAlign: 'center' }}>
          <Typography variant="body2">
            Reconnecting to chat... (Attempt {reconnectAttempt + 1}/5)
          </Typography>
        </Box>
      )}
      {!isConnected && selectedHackathon && reconnectAttempt >= 5 && (
        <Box sx={{ p: 1, bgcolor: 'error.light', color: 'error.contrastText', textAlign: 'center' }}>
          <Typography variant="body2">
            Failed to connect to chat. Please refresh the page.
          </Typography>
        </Box>
      )}
      <Box sx={{ flexGrow: 1, overflow: 'auto', mb: 2, p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
        {messages.map((message, index) => (
          <Box
            key={index}
            sx={{
              mb: 2,
              display: 'flex',
              flexDirection: 'column',
              alignItems: message.sender === user.name ? 'flex-end' : 'flex-start',
            }}
          >
            <Typography variant="caption" color="text.secondary">
              {message.sender}
            </Typography>
            <MessageBubble isOwn={message.sender === user.name}>
              {message.type === "FILE" && message.content.mimeType?.startsWith('image/') ? (
                <a
                  href={`${ApiService.BASE_URL}${message.content.url}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{ display: "block" }}
                >
                  <img
                    src={`${ApiService.BASE_URL}${message.content.url}`}
                    alt={message.content.fileName || "Uploaded image"}
                    style={{
                      maxWidth: "100%",
                      maxHeight: "200px",
                      borderRadius: "8px",
                      objectFit: "cover",
                      cursor: "pointer",
                    }}
                  />
                </a>
              ) : message.type === "FILE" ? (
                <div style={{ marginTop: 4 }}>
                  {/* File name row */}
                  <div
                    style={{ display: "flex", alignItems: "center", gap: 8 }}
                  >
                    <FileIcon mimeType={message.content.mimeType} />
                    <a
                      href={`${ApiService.BASE_URL}${message.content.url}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      style={{
                        fontWeight: 500,
                        color: "inherit",
                        textDecoration: "none",
                        "&:hover": { textDecoration: "underline" },
                      }}
                    >
                      {message.content.fileName}
                    </a>
                  </div>

                  {/* Meta info row */}
                  <div
                    style={{
                      display: "flex",
                      gap: 12,
                      fontSize: 12,
                      color: "#666",
                      marginTop: 2,
                      marginLeft: 24, // Align with icon
                    }}
                  >
                    <span>{cleanMimeType(message.content.mimeType)}</span>
                    <span>{formatFileSize(message.content.fileSize)}</span>
                  </div>
                </div>
              ) : (
                <Typography variant="body2">{message.content}</Typography>
              )}
              <Typography
                variant="caption"
                display="block"
                mt={0.5}
                textAlign={message.sender === user.name ? "right" : "left"}
              >
                {message.sender} —{" "}
                {new Date(message.timestamp).toLocaleTimeString()}
              </Typography>
            </MessageBubble>
            <Typography variant="caption" color="text.secondary">
              {new Date(message.timestamp).toLocaleTimeString()}
            </Typography>
          </Box>
        ))}
        <div ref={messagesEndRef} />
      </Box>

      <Box sx={{ 
        display: 'flex', 
        gap: 1, 
        alignItems: 'center',
        bgcolor: 'background.paper',
        p: 1,
        borderRadius: 1
      }}>
        <TextField
          fullWidth
          variant="outlined"
          placeholder="Введите сообщение..."
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
          size="small"
          sx={{
            '& .MuiOutlinedInput-root': {
              borderRadius: '20px',
            }
          }}
        />
        <input
          type="file"
          ref={fileInputRef}
          style={{ display: 'none' }}
          onChange={handleFileUpload}
        />
        <Button
          type="text"
          icon={<PaperClipOutlined />}
          onClick={() => fileInputRef.current?.click()}
          style={{
            fontSize: '18px',
            color: '#b29262',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            width: '40px',
            height: '40px',
            borderRadius: '50%',
            padding: 0
          }}
        />
        <Button
          type="text"
          icon={<SendOutlined />}
          onClick={handleSendMessage}
          disabled={!newMessage.trim()}
          style={{
            fontSize: '18px',
            color: newMessage.trim() ? '#b29262' : 'rgba(0, 0, 0, 0.25)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            width: '40px',
            height: '40px',
            borderRadius: '50%',
            padding: 0
          }}
        />
      </Box>
    </Box>
  );
};

export default Chat;
