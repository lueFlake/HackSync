import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
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
  Col,
  message
} from 'antd';
import { LockOutlined, UserOutlined, MailOutlined } from '@ant-design/icons';
import PageContainer from '../components/PageContainer';
import { useAuth } from '../contexts/AuthContext';

const { Title } = Typography;

const LoginPage = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const location = useLocation();
  const { login, register } = useAuth();
  const [isLoginForm, setIsLoginForm] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (values) => {
    setLoading(true);
    setError(null);

    try {
      if (isLoginForm) {
        await login(values);
        // Перенаправляем на предыдущую страницу или на главную
        const from = location.state?.from?.pathname || '/';
        navigate(from, { replace: true });
        message.success('Вход выполнен успешно');
      } else {
        await register(values);
        setIsLoginForm(true);
        form.resetFields();
        message.success('Регистрация прошла успешно. Теперь вы можете войти.');
      }
    } catch (err) {
      console.error('Auth error:', err);
      setError(err.response?.data?.message || err.message || 'Произошла ошибка');
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
            <Card style={{
              borderRadius: '8px',
              boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
              maxWidth: '100%'
            }}>
              <div style={{ textAlign: 'center', marginBottom: 24 }}>
                <Title level={3}>
                  {isLoginForm ? 'Вход в систему' : 'Регистрация'}
                </Title>
                {location.state?.from && (
                  <div style={{ color: '#666', marginTop: 8 }}>
                    Для доступа к этой странице требуется авторизация
                  </div>
                )}
              </div>

              {error && (
                <Alert
                  message={error}
                  type="error"
                  showIcon
                  style={{ marginBottom: 24 }}
                  closable
                  onClose={() => setError(null)}
                />
              )}

              <Form
                form={form}
                layout="vertical"
                onFinish={handleSubmit}
                initialValues={{ remember: true }}
                size="large"
              >
                {!isLoginForm && (
                  <Form.Item
                    name="name"
                    rules={[{
                      required: true,
                      message: 'Пожалуйста, введите ваше имя',
                      whitespace: true
                    }]}
                    hasFeedback
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
                    {
                      required: true,
                      message: 'Пожалуйста, введите email'
                    },
                    {
                      type: 'email',
                      message: 'Некорректный email'
                    }
                  ]}
                  hasFeedback
                >
                  <Input
                    prefix={<MailOutlined />}
                    placeholder="Email"
                    type="email"
                    autoComplete="username"
                  />
                </Form.Item>

                <Form.Item
                  name="password"
                  rules={[
                    {
                      required: true,
                      message: 'Пожалуйста, введите пароль'
                    },
                    {
                      min: 6,
                      message: 'Пароль должен быть не менее 6 символов'
                    }
                  ]}
                  hasFeedback
                >
                  <Input.Password
                    prefix={<LockOutlined />}
                    placeholder="Пароль"
                    autoComplete={isLoginForm ? 'current-password' : 'new-password'}
                  />
                </Form.Item>

                {!isLoginForm && (
                  <Form.Item
                    name="confirmPassword"
                    dependencies={['password']}
                    rules={[
                      {
                        required: true,
                        message: 'Пожалуйста, подтвердите пароль'
                      },
                      ({ getFieldValue }) => ({
                        validator(_, value) {
                          if (!value || getFieldValue('password') === value) {
                            return Promise.resolve();
                          }
                          return Promise.reject(new Error('Пароли не совпадают'));
                        },
                      }),
                    ]}
                    hasFeedback
                  >
                    <Input.Password
                      prefix={<LockOutlined />}
                      placeholder="Подтвердите пароль"
                      autoComplete="new-password"
                    />
                  </Form.Item>
                )}

                <Form.Item>
                  <Button
                    type="primary"
                    htmlType="submit"
                    block
                    loading={loading}
                    size="large"
                  >
                    {isLoginForm ? 'Войти' : 'Зарегистрироваться'}
                  </Button>
                </Form.Item>

                <Divider />

                <div style={{ textAlign: 'center' }}>
                  <Button
                    type="link"
                    onClick={toggleForm}
                    style={{ padding: 0 }}
                  >
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