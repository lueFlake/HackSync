import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Card,
  Typography,
  Button,
  Avatar,
  Divider,
  Space,
  Row,
  Col,
  List,
  Tag
} from 'antd';
import {
  UserOutlined,
  MailOutlined,
  EditOutlined,
  LogoutOutlined,
  TrophyOutlined,
  TeamOutlined
} from '@ant-design/icons';
import PageContainer from '../components/PageContainer';

const { Title, Text } = Typography;

const ProfilePage = () => {
  const navigate = useNavigate();

  // Моковые данные пользователя
  const [user, setUser] = useState({
    name: 'Иван Иванов',
    email: 'ivan@example.com',
    role: 'Fullstack разработчик',
    teams: [
      {
        id: 1,
        name: 'Quantum Hackers',
        event: 'Hackathon 2023',
        position: '1 место',
        role: 'Тимлид',
        technologies: ['React', 'Node.js', 'MongoDB']
      },
      {
        id: 2,
        name: 'Code Breakers',
        event: 'AI Challenge 2022',
        position: 'Финалисты',
        role: 'Backend разработчик',
        technologies: ['Python', 'TensorFlow', 'Docker']
      },
      {
        id: 3,
        name: 'Byte Force',
        event: 'Blockchain Weekend',
        position: 'Участники',
        role: 'Solidity разработчик',
        technologies: ['Solidity', 'Web3.js', 'Ethereum']
      }
    ]
  });

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <PageContainer>
      <Row gutter={[16, 16]}>
        <Col xs={24} md={8}>
          <Card style={{ textAlign: 'center' }}>
            <Avatar
              size={128}
              icon={<UserOutlined />}
              style={{ marginBottom: 16, fontSize: 64 }}
            />
            <Title level={3}>{user.name}</Title>
            <Text type="secondary">{user.role}</Text>

            <Divider />

            <Space direction="vertical" style={{ width: '100%' }}>
              <Button
                type="primary"
                icon={<EditOutlined />}
                block
              >
                Редактировать профиль
              </Button>
              <Button
                danger
                icon={<LogoutOutlined />}
                onClick={handleLogout}
                block
              >
                Выйти
              </Button>
            </Space>

            <Divider />

            <div style={{ textAlign: 'left' }}>
              <p><MailOutlined /> {user.email}</p>
            </div>
          </Card>
        </Col>

        <Col xs={24} md={16}>
          <Card title={
            <span>
              <TeamOutlined style={{ marginRight: 8 }} />
              Мои команды в хакатонах
            </span>
          }>
            <List
              itemLayout="vertical"
              dataSource={user.teams}
              renderItem={team => (
                <List.Item
                  key={team.id}
                  extra={
                    <Tag icon={<TrophyOutlined />} color={team.position.includes('1') ? 'gold' : 'default'}>
                      {team.position}
                    </Tag>
                  }
                >
                  <List.Item.Meta
                    title={team.name}
                    description={team.event}
                  />
                  <div style={{ marginTop: 8 }}>
                    <Text strong>Роль: </Text>
                    <Text>{team.role}</Text>
                  </div>
                  <div style={{ marginTop: 8 }}>
                    <Text strong>Технологии: </Text>
                    <Space size={[0, 8]} wrap style={{ marginTop: 4 }}>
                      {team.technologies.map(tech => (
                        <Tag key={tech}>{tech}</Tag>
                      ))}
                    </Space>
                  </div>
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>
    </PageContainer>
  );
};

export default ProfilePage;