import {
  EditOutlined,
  LogoutOutlined,
  MailOutlined,
  TeamOutlined,
  TrophyOutlined,
  UserAddOutlined,
  UserOutlined
} from '@ant-design/icons';
import {
  Avatar,
  Button,
  Card,
  Col,
  Divider,
  Form,
  Input,
  List,
  message,
  Modal,
  Row,
  Space,
  Spin,
  Tag,
  Typography
} from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import PageContainer from '../components/PageContainer';
import { ApiService } from '../services/ApiService';
import AuthService from '../services/AuthService';

const { Title, Text } = Typography;

const ProfilePage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null);
  const [teams, setTeams] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  const fileInputRef = useRef(null);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const userData = await AuthService.getCurrentUser();
        setUser(userData);

        // Fetch user's teams
        const userTeams = await ApiService.request(`/users/${userData.userId}/teams`, "GET", null, true);
        setTeams(userTeams);
      } catch (error) {
        console.error('Failed to fetch user data:', error);
        // Handle error appropriately
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, []);

  const handleLogout = async () => {
    try {
      await ApiService.logout();
      localStorage.removeItem('token');
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const showModal = () => {
    form.setFieldsValue({
      name: user.name,
      email: user.email
    });
    setIsModalVisible(true);
  };

  const handleCancel = () => {
    setIsModalVisible(false);
    form.resetFields();
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const updatedUser = await ApiService.updateUser(user.userId, values);
      setUser(updatedUser);
      setIsModalVisible(false);
      message.success('Профиль успешно обновлен');
    } catch (error) {
      console.error('Failed to update profile:', error);
      message.error('Не удалось обновить профиль');
    }
  };

  if (loading) {
    return (
      <PageContainer>
        <div style={{ textAlign: 'center', padding: '50px' }}>
          <Spin size="large" />
        </div>
      </PageContainer>
    );
  }

  if (!user) {
    return (
      <PageContainer>
        <div style={{ textAlign: 'center', padding: '50px' }}>
          <Text>Failed to load user data</Text>
        </div>
      </PageContainer>
    );
  }

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
                onClick={showModal}
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
              dataSource={teams}
              renderItem={team => (
                <List.Item
                  key={team.id}
                  extra={
                    <Tag icon={<TrophyOutlined />} color={team.position === 1 ? 'gold' : 'default'}>
                      {team.position === 1 ? '1 место' : team.position === 2 ? '2 место' : team.position === 3 ? '3 место' : 'Участник'}
                    </Tag>
                  }
                >
                  <List.Item.Meta
                    title={team.name}
                    description={team.event}
                  />
                  <div style={{ marginTop: 8 }}>
                    <Text strong>Участники команды: </Text>
                    <Space size={[0, 8]} wrap style={{ marginTop: 4 }}>
                      {team.members?.map(member => (
                        <Link
                          key={member.id}
                          to={`/profile/${member.id}`}
                          style={{ marginRight: 8 }}
                        >
                          <Tag icon={<UserAddOutlined />}>
                            {member.name}
                          </Tag>
                        </Link>
                      ))}
                    </Space>
                  </div>
                  <div style={{ marginTop: 8 }}>
                    <Text strong>Технологии: </Text>
                    <Space size={[0, 8]} wrap style={{ marginTop: 4 }}>
                      {team.technologies?.map(tech => (
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

      <Modal
        title="Редактировать профиль"
        open={isModalVisible}
        onOk={handleSubmit}
        onCancel={handleCancel}
        okText="Сохранить"
        cancelText="Отмена"
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="name"
            label="Имя"
            rules={[
              {
                required: true,
                message: 'Пожалуйста, введите ваше имя',
              },
            ]}
          >
            <Input prefix={<UserOutlined />} placeholder="Введите ваше имя" />
          </Form.Item>
          <Form.Item
            name="email"
            label="Email"
            rules={[
              {
                required: true,
                message: 'Пожалуйста, введите ваш email',
              },
              {
                type: 'email',
                message: 'Пожалуйста, введите корректный email',
              },
            ]}
          >
            <Input prefix={<MailOutlined />} placeholder="Введите ваш email" />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
};

export default ProfilePage;