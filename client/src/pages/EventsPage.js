import React, { useState } from 'react';
import { useRecoilState } from 'recoil';
import { Button, Form, Input, DatePicker, Divider, notification } from 'antd';
import EventsTable from '../components/EventsTable';
import { eventsState } from '../state/events';
import { ApiService } from '../services/api';
import PageContainer from '../components/PageContainer';
import { Typography } from '@mui/material';

const EventsPage = () => {
  const [events, setEvents] = useRecoilState(eventsState);
  const [isCreating, setIsCreating] = useState(false);
  const [form] = Form.useForm();

  const handleCreateStart = () => setIsCreating(true);
  
  const handleCancel = () => {
    form.resetFields();
    setIsCreating(false);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const newEvent = await ApiService.createEvent(values);
      
      setEvents([...events, newEvent]);
      notification.success({ message: 'Событие создано!' });
      handleCancel();
    } catch (error) {
      notification.error({ message: 'Ошибка создания события' });
    }
  };

  const handleDelete = async (id) => {
    try {
      await ApiService.deleteEvent(id);
      setEvents(events.filter(e => e.id !== id));
      notification.success({ message: 'Событие удалено' });
    } catch (error) {
      notification.error({ message: 'Ошибка удаления' });
    }
  };

  const handleEdit = (event) => {
    // Логика редактирования (можно реализовать через модалку или инлайн-редактирование)
    notification.info({ message: `Редактирование события: ${event.name}` });
  };

  return (
    <PageContainer title={"События"}>
      <Button
        type="primary"
        onClick={handleCreateStart}
        disabled={isCreating}
      >
        Создать событие
      </Button>

      {isCreating && (
        <>
          <Divider orientation="left">Новое событие</Divider>
          
          <Form
            form={form}
            layout="vertical"
            style={{ maxWidth: 600, marginBottom: 24 }}
          >
            <Form.Item
              name="name"
              label="Название события"
              rules={[{ required: true, message: 'Введите название' }]}
            >
              <Input placeholder="Хакатон 2024" />
            </Form.Item>

            <Form.Item
              name="date"
              label="Дата проведения"
              rules={[{ required: true, message: 'Укажите дату' }]}
            >
              <DatePicker 
                showTime 
                style={{ width: '100%' }} 
                format="DD.MM.YYYY HH:mm"
              />
            </Form.Item>

            <Form.Item>
              <Button 
                type="primary" 
                onClick={handleSubmit}
              >
                Сохранить
              </Button>
              <Button onClick={handleCancel}>
                Отмена
              </Button>
            </Form.Item>
          </Form>
        </>
      )}
      <Divider />

      <EventsTable
        events={events}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />
    </PageContainer>
  );
};

export default EventsPage;