import {
  CameraOutlined,
  EditOutlined,
  LogoutOutlined,
  MailOutlined,
  TeamOutlined,
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
  Typography,
  Upload
} from 'antd';
import React, { useEffect, useState } from 'react';
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
  const [uploadingAvatar, setUploadingAvatar] = useState(false);
  const [avatarUrl, setAvatarUrl] = useState(null);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const userData = await AuthService.getCurrentUser();
        setUser(userData);
        if (userData.avatarUrl) {
          setAvatarUrl(ApiService.getUserAvatarUrl(userData.userId));
        }

        // Fetch user's teams
        const teamIds = await ApiService.request(`/teams/users/${userData.userId}`, "GET", null, true);
        const teamsWithDetails = await Promise.all(
          teamIds.map(async (teamId) => {
            const teamDetails = await ApiService.request(`/teams/${teamId}`, "GET", null, true);
            const members = await ApiService.request(`/teams/${teamId}/members`, "GET", null, true);
            const memberDetails = await Promise.all(
              members.map(async (memberId) => {
                const userDetails = await ApiService.request(`/users/${memberId}`, "GET", null, true);
                return {
                  id: memberId,
                  name: userDetails.name,
                  email: userDetails.email
                };
              })
            );
            return {
              ...teamDetails,
              members: memberDetails
            };
          })
        );
        setTeams(teamsWithDetails);
      } catch (error) {
        console.error('Failed to fetch user data:', error);
        message.error('Failed to load user data');
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
      console.error('Не удалось обновить профиль:', error);
      message.error('Не удалось обновить профиль');
    }
  };

  const handleAvatarUpload = async (file) => {
    try {
      setUploadingAvatar(true);
      const result = await ApiService.uploadUserAvatar(user.userId, file);
      setAvatarUrl(ApiService.getUserAvatarUrl(user.userId));
      message.success('Avatar uploaded successfully');
      return false; // Prevent default upload behavior
    } catch (error) {
      message.error(error.message || 'Failed to upload avatar');
      return false;
    } finally {
      setUploadingAvatar(false);
    }
  };

  const beforeUpload = (file) => {
    const isImage = file.type.startsWith('image/');
    if (!isImage) {
      message.error('You can only upload image files!');
      return false;
    }

    const isLt5M = file.size / 1024 / 1024 < 5;
    if (!isLt5M) {
      message.error('Image must be smaller than 5MB!');
      return false;
    }

    return true;
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
            <div style={{
              marginBottom: 16,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: '16px'
            }}>
              <Avatar
                size={160}
                src={avatarUrl}
                icon={<UserOutlined />}
                style={{ fontSize: 80 }}
              />
              <Upload
                name="avatar"
                showUploadList={false}
                beforeUpload={beforeUpload}
                customRequest={({ file }) => handleAvatarUpload(file)}
                style={{ width: '100%', maxWidth: '200px' }}
              >
                <Button
                  type="primary"
                  icon={<CameraOutlined />}
                  loading={uploadingAvatar}
                  style={{
                    width: '100%',
                    height: 'clamp(32px, 5vw, 40px)',
                    fontSize: 'clamp(12px, 2vw, 14px)'
                  }}
                >
                  Загрузить фото
                </Button>
              </Upload>
            </div>
            <Title level={3} style={{ fontSize: 'clamp(18px, 3vw, 24px)' }}>{user.name}</Title>

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