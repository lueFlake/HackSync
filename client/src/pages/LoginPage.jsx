import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Form,
  Input,
  Button,
  Card,
  Typography,
  Divider,
  Alert,
  Layout,
  Row,
  Col
} from 'antd';
import { LockOutlined, UserOutlined, MailOutlined } from '@ant-design/icons';
import PageContainer from '../components/PageContainer';
import { ApiService } from '../services/api';

const { Title } = Typography;

const LoginPage = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const [isLoginForm, setIsLoginForm] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (values) => {
    setLoading(true);
    setError(null);

    try {
      if (isLoginForm) {
        // для пропуска любого email и пароль
        if (values.email && values.password) {
          const mockToken = 'mock_token_'
          localStorage.setItem('token', mockToken);
          window.location.href = '/';
          // navigate('/');
          return;
        }

        const response = await ApiService.login(values);
        localStorage.setItem('token', response.token);
        navigate('/');
      } else {
        await ApiService.register(values);
        setIsLoginForm(true);
        form.resetFields();
        Alert.success('Регистрация выполнена.');
      }
    } catch (err) {
      setError(err.message || 'Произошла ошибка');
    } finally {
      setLoading(false);
    }
  };

  const toggleForm = () => {
    setIsLoginForm(!isLoginForm);
    form.resetFields();
    setError(null);
  };

  return (
    <PageContainer hideHeader>
      <Layout style={{ minHeight: '70vh', background: '#ffffff' }}>
        <Row justify="center" align="middle" style={{ flex: 1 }}>
          <Col xs={24} sm={20} md={16} lg={12} xl={8}>
            <Card style={{ borderRadius: '8px', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>
              <div style={{ textAlign: 'center', marginBottom: 24 }}>
                <Title level={3}>
                  {isLoginForm ? 'Вход в систему' : 'Регистрация'}
                </Title>
              </div>

              {error && (
                <Alert
                  message={error}
                  type="error"
                  showIcon
                  style={{ marginBottom: 24 }}
                />
              )}

              <Form
                form={form}
                layout="vertical"
                onFinish={handleSubmit}
                initialValues={{ remember: true }}
              >
                {!isLoginForm && (
                  <Form.Item
                    name="name"
                    rules={[{ required: true, message: 'Пожалуйста, введите ваше имя' }]}
                  >
                    <Input
                      prefix={<UserOutlined />}
                      placeholder="Ваше имя"
                    />
                  </Form.Item>
                )}

                <Form.Item
                  name="email"
                  rules={[
                    { required: true, message: 'Пожалуйста, введите email' },
                    { type: 'email', message: 'Некорректный email' }
                  ]}
                >
                  <Input
                    prefix={<MailOutlined />}
                    placeholder="Email"
                    type="email"
                  />
                </Form.Item>

                <Form.Item
                  name="password"
                  rules={[
                    { required: true, message: 'Пожалуйста, введите пароль' },
                    { min: 1, message: 'Пароль должен быть не менее 6 символов' }
                  ]}
                >
                  <Input.Password
                    prefix={<LockOutlined />}
                    placeholder="Пароль"
                  />
                </Form.Item>

                {!isLoginForm && (
                  <Form.Item
                    name="confirmPassword"
                    dependencies={['password']}
                    rules={[
                      { required: true, message: 'Пожалуйста, подтвердите пароль' },
                      ({ getFieldValue }) => ({
                        validator(_, value) {
                          if (!value || getFieldValue('password') === value) {
                            return Promise.resolve();
                          }
                          return Promise.reject(new Error('Пароли не совпадают'));
                        },
                      }),
                    ]}
                  >
                    <Input.Password
                      prefix={<LockOutlined />}
                      placeholder="Подтвердите пароль"
                    />
                  </Form.Item>
                )}

                <Form.Item>
                  <Button
                    type="primary"
                    htmlType="submit"
                    block
                    loading={loading}
                  >
                    {isLoginForm ? 'Войти' : 'Зарегистрироваться'}
                  </Button>
                </Form.Item>

                <Divider />

                <div style={{ textAlign: 'center' }}>
                  <Button type="link" onClick={toggleForm}>
                    {isLoginForm
                      ? 'Нет аккаунта? Зарегистрироваться'
                      : 'Уже есть аккаунт? Войти'}
                  </Button>
                </div>
              </Form>
            </Card>
          </Col>
        </Row>
      </Layout>
    </PageContainer>
  );
};

export default LoginPage;