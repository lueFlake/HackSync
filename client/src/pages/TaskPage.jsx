import {
  ClockCircleOutlined,
  FlagOutlined,
  UserOutlined,
} from "@ant-design/icons";
import {
  Avatar,
  Card,
  Descriptions,
  message,
  Space,
  Tag,
  Typography,
} from "antd";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { ApiService } from "../services/ApiService";

const TaskPage = () => {
  const { id } = useParams();
  const [task, setTask] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTask = async () => {
      try {
        setLoading(true);
        const response = await ApiService.getTaskById(id);
        setTask({
          id: response.id.toString(),
          title: response.name,
          description: response.description,
          priority: response.priority.toLowerCase(),
          status: response.status.name.toLowerCase().replace(" ", "_"),
          createdAt: response.createdAt,
          author: {
            id: response.userId,
            name: response.userId, // Replace with actual user name if available
            avatar: null,
          },
        });
      } catch (error) {
        message.error("Не удалось загрузить задачу");
        console.error(error);
      } finally {
        setLoading(false);
      }
    };
    fetchTask();
  }, [id]);

  if (loading) {
    return <div>Загрузка...</div>;
  }

  if (!task) {
    return <div>Задача не найдена</div>;
  }

  return (
    <Card
      title={task.title}
      bordered={false}
      style={{ maxWidth: 800, margin: "0 auto" }}
    >
      <Descriptions column={1} bordered>
        <Descriptions.Item label="Автор">
          <Space>
            <Avatar src={task.author.avatar} icon={<UserOutlined />} />
            <span>{task.author.name}</span>
          </Space>
        </Descriptions.Item>

        <Descriptions.Item label="Описание">
          <Typography.Text>{task.description}</Typography.Text>
        </Descriptions.Item>

        <Descriptions.Item label="Приоритет">
          <Tag
            color={
              task.priority === "high"
                ? "red"
                : task.priority === "medium"
                ? "orange"
                : "green"
            }
          >
            <FlagOutlined /> {task.priority}
          </Tag>
        </Descriptions.Item>

        <Descriptions.Item label="Статус">
          <Tag
            color={
              task.status === "done"
                ? "green"
                : task.status === "in_progress"
                ? "blue"
                : "default"
            }
          >
            {task.status}
          </Tag>
        </Descriptions.Item>

        <Descriptions.Item label="Создана">
          <Space>
            <ClockCircleOutlined />
            {new Date(task.createdAt).toLocaleString()}
          </Space>
        </Descriptions.Item>
      </Descriptions>
    </Card>
  );
};

export default TaskPage;
