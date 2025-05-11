import { Button, DatePicker, Form, Input, Select, Space } from 'antd';
import React from 'react';

const { Option } = Select;

const DeadlineForm = ({ form, onSubmit, onCancel, isEditing }) => {
  return (
    <Form
      form={form}
      onFinish={onSubmit}
      layout="vertical"
    >
      <Form.Item
        name="name"
        label="Название дедлайна"
        rules={[{ required: true, message: 'Пожалуйста, введите название дедлайна' }]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        name="type"
        label="Тип"
        rules={[{ required: true, message: 'Пожалуйста, выберите тип дедлайна' }]}
      >
        <Select>
          <Option value="PROJECT">Проект</Option>
          <Option value="PRESENTATION">Презентация</Option>
          <Option value="REGISTRATION">Регистрация</Option>
          <Option value="OTHER">Другое</Option>
        </Select>
      </Form.Item>
      <Form.Item
        name="date"
        label="Дата"
        rules={[{ required: true, message: 'Пожалуйста, выберите дату' }]}
      >
        <DatePicker showTime style={{ width: '100%' }} />
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

export default DeadlineForm; 