import {
  FileExcelOutlined, // Text
  FileImageOutlined, // Archives
  FileOutlined,
  FilePdfOutlined, // Images
  FilePptOutlined, // Excel
  FileTextOutlined, // PDF
  FileWordOutlined,
  PaperClipOutlined,
  SendOutlined,
} from "@ant-design/icons";
import { Box, List, Paper, TextField, Typography } from "@mui/material";
import { styled } from "@mui/material/styles";
import { Button } from "antd";
import React, { useEffect, useRef, useState } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { ApiService } from "../../services/ApiService";

const ChatContainer = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(2),
  height: "600px",
  display: "flex",
  flexDirection: "column",
}));

const MessageList = styled(List)({
  flex: 1,
  overflow: "auto",
  marginBottom: "16px",
});

const MessageInput = styled(Box)({
  display: "flex",
  gap: "8px",
});

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
  const [connected, setConnected] = useState(false);
  const [ws, setWs] = useState(null);
  const messagesEndRef = useRef(null);
  const fileInputRef = useRef(null);
  const { user, isLoading, isAuthenticated } = useAuth();

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

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const history = await ApiService.chatHistory();
        setMessages(history);
      } catch (error) {
        console.error("Error fetching chat history:", error);
      }
    };

    fetchHistory();
  }, []);

  useEffect(() => {
    const websocket = new WebSocket(
      `ws://${ApiService.BASE_URL.replace("http://", "").replace(
        "https://",
        ""
      )}/chat`
    );

    websocket.onopen = () => {
      setConnected(true);
    };

    websocket.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data);
        setMessages((prev) => [...prev, message]);
      } catch (e) {
        const textMessage = {
          sender: "System",
          content: event.data,
          timestamp: Date.now(),
          type: "TEXT",
        };
        setMessages((prev) => [...prev, textMessage]);
      }
    };

    websocket.onclose = () => setConnected(false);
    websocket.onerror = () => setConnected(false);

    setWs(websocket);

    return () => websocket.close();
  }, []);

  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  const handleSendMessage = () => {
    if (!connected || !newMessage.trim() || !ws) return;

    const message = {
      sender: user.name,
      content: newMessage,
      timestamp: Date.now(),
      type: "TEXT",
    };

    ws.send(JSON.stringify(message));
    setNewMessage("");
  };

  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    e.target.value = "";

    const formData = new FormData();
    formData.append("file", file);

    console.log(formData);

    try {
      const res = await fetch(`${ApiService.BASE_URL}/chat/upload`, {
        method: "POST",
        body: formData,
      });
      const data = await res.json();

      const message = {
        sender: user.name,
        content: {
          url: data.url,
          mimeType: file.type,
          fileName: file.name,
          fileSize: file.size,
        },
        timestamp: Date.now(),
        type: file.type.startsWith("image/") ? "IMAGE" : "FILE",
      };

      ws.send(JSON.stringify(message));
    } catch (err) {
      console.error("Upload failed", err);
    }
  };

  if (isLoading) return <div>Loading...</div>;
  if (!isAuthenticated) return <div>Please log in</div>;

  return (
    <ChatContainer elevation={3} sx={{ m: 0.5 }}>
      <MessageList>
        {messages.map((message, index) => {
          const isOwn = message.sender === user.name;
          return (
            <Box
              key={index}
              display="flex"
              justifyContent={isOwn ? "flex-end" : "flex-start"}
            >
              <MessageBubble isOwn={isOwn}>
                {message.type === "IMAGE" ? (
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
                  <a
                    href={message.content}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    {message.fileName}
                  </a>
                ) : (
                  <Typography variant="body2">{message.content}</Typography>
                )}
                {message.type === "FILE" && (
                  <div style={{ marginTop: 4 }}>
                    {/* File name row */}
                    <div
                      style={{ display: "flex", alignItems: "center", gap: 8 }}
                    >
                      <FileIcon mimeType={message.content.mimeType} />
                      <a
                        href={`${ApiService.BASE_URL}/${message.content.url}`}
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
                )}
                <Typography
                  variant="caption"
                  display="block"
                  mt={0.5}
                  textAlign={isOwn ? "right" : "left"}
                >
                  {message.sender} —{" "}
                  {new Date(message.timestamp).toLocaleTimeString()}
                </Typography>
              </MessageBubble>
            </Box>
          );
        })}
        <div ref={messagesEndRef} />
      </MessageList>
      <MessageInput>
        <TextField
          fullWidth
          placeholder={connected ? "Type a message..." : "Connecting..."}
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          onKeyPress={(e) => e.key === "Enter" && handleSendMessage()}
          disabled={!connected}
        />
        <input
          type="file"
          ref={fileInputRef}
          style={{ display: "none" }}
          onChange={handleFileChange}
        />
        <Button
          type="text"
          icon={<PaperClipOutlined />}
          onClick={() => fileInputRef.current.click()}
          disabled={!connected}
          style={{
            fontSize: "18px",
            color: newMessage ? "#b29262" : "rgba(0, 0, 0, 0.25)",
          }}
        />

        {/* Send Button (icon version) */}
        <Button
          type="text"
          icon={<SendOutlined />}
          onClick={handleSendMessage}
          disabled={!newMessage.trim() || !connected}
          style={{
            fontSize: "18px",
            color: newMessage ? "#b29262" : "rgba(0, 0, 0, 0.25)",
          }}
        />
      </MessageInput>
    </ChatContainer>
  );
};

export default Chat;
