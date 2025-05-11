import { UserOutlined } from '@ant-design/icons';
import { Button, Form, Input, Select, Space } from 'antd';
import React, { useEffect, useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { ApiService } from '../../services/ApiService';

const TeamForm = ({ form, onSubmit, onCancel, isEditing }) => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const { user } = useAuth();

  useEffect(() => {
    loadUsers();
  }, []);

  // Add effect to handle initial form values when editing
  useEffect(() => {
    const initializeForm = async () => {
      if (isEditing) {
        const teamId = form.getFieldValue('id');
        if (teamId) {
          try {
            const response = await ApiService.get(`/teams/${teamId}/members`);
            const memberIds = response.data.map(member => member.id);
            form.setFieldsValue({ members: memberIds });
          } catch (error) {
            console.error('Failed to load team members:', error);
          }
        }
      } else if (user) {
        // Pre-select current user when creating a new team and ensure they are first in the list
        form.setFieldsValue({ members: [user.userId] });
      }
    };
    initializeForm();
  }, [isEditing, form, user]);

  const loadUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await ApiService.getUsers();
      // Filter out the current user from the list since they are already added
      const otherUsers = response?.filter(u => u.id !== user?.userId) || [];
      setUsers(otherUsers);
    } catch (error) {
      console.error('Failed to load users:', error);
      setError('Не удалось загрузить список пользователей');
      setUsers([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Form
      form={form}
      onFinish={onSubmit}
      layout="vertical"
    >
      <Form.Item
        name="name"
        label="Название команды"
        rules={[{ required: true, message: 'Пожалуйста, введите название команды' }]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        name="members"
        label="Участники"
        validateStatus={error ? 'error' : undefined}
        help={error}
      >
        <Select
          mode="multiple"
          placeholder="Выберите участников"
          loading={loading}
          optionFilterProp="children"
          showSearch
          style={{ width: '100%' }}
          optionLabelProp="label"
        >
          {users.map(user => (
            <Select.Option 
              key={user.id} 
              value={user.id}
              label={`${user.name} (${user.email})`}
            >
              <Space>
                <UserOutlined />
                {user.name} ({user.email})
              </Space>
            </Select.Option>
          ))}
        </Select>
      </Form.Item>
      <Form.Item>
        <Space>
          <Button type="primary" htmlType="submit">
            {isEditing ? 'Обновить' : 'Создать'}
          </Button>
          <Button onClick={onCancel}>
            Отмена
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default TeamForm; 