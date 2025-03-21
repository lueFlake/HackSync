import React from 'react';
import { Table, Button, Popconfirm } from 'antd';

const EventsTable = ({ events, onEdit, onDelete }) => {
  const columns = [
    {
      title: 'Название',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Дата',
      dataIndex: 'date',
      key: 'date',
      render: (date) => new Date(date).toLocaleString(),
    },
    {
      title: 'Действия',
      key: 'actions',
      render: (_, record) => (
        <div className="table-actions">
          <Button 
            type="link" 
            onClick={() => onEdit(record)}
          >
            Редактировать
          </Button>
          
          <Popconfirm
            title="Удалить событие?"
            onConfirm={() => onDelete(record.id)}
            okText="Да"
            cancelText="Нет"
          >
            <Button type="link" danger>
              Удалить
            </Button>
          </Popconfirm>
        </div>
      ),
    },
  ];

  return (
    <Table
      rowKey="id"
      dataSource={events}
      columns={columns}
      pagination={{ pageSize: 10 }}
    />
  );
};

export default EventsTable;