import { Card, Descriptions, Tag, Space, Typography, Avatar } from 'antd';
import { UserOutlined, ClockCircleOutlined, FlagOutlined } from '@ant-design/icons';

const TaskPage = ({ task }) => {
  return (
    <Card
      title={task.title}
      bordered={false}
      style={{ maxWidth: 800, margin: '0 auto' }}
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
          <Tag color={
            task.priority === 'high' ? 'red' :
              task.priority === 'medium' ? 'orange' : 'green'
          }>
            <FlagOutlined /> {task.priority}
          </Tag>
        </Descriptions.Item>

        <Descriptions.Item label="Статус">
          <Tag color={
            task.status === 'done' ? 'green' :
              task.status === 'in_progress' ? 'blue' : 'default'
          }>
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

// mockTask !!!
const mockTask = {
  id: 1,
  title: 'Реализовать авторизацию',
  description: 'Необходимо добавить JWT аутентификацию на бэкенде',
  priority: 'high',
  status: 'in_progress',
  createdAt: '2023-05-15T10:30:00Z',
  author: {
    id: 1,
    name: 'Иван Петров',
    avatar: null
  }
};

<TaskPage task={mockTask} />

export default TaskPage;