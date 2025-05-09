import { DeleteOutlined, EditOutlined } from '@ant-design/icons';
import { Button, Popconfirm, Space, Table } from 'antd';
import moment from 'moment';
import React from 'react';

const EventsTable = ({ data, onEdit, onDelete }) => {
  const columns = [
    {
      title: 'Название',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Описание',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: 'Дата регистрации',
      dataIndex: 'dateOfRegister',
      key: 'dateOfRegister',
      render: (date) => date ? moment(date).format('DD.MM.YYYY HH:mm') : '-',
    },
    {
      title: 'Дата начала',
      dataIndex: 'dateOfStart',
      key: 'dateOfStart',
      render: (date) => date ? moment(date).format('DD.MM.YYYY HH:mm') : '-',
    },
    {
      title: 'Дата окончания',
      dataIndex: 'dateOfEnd',
      key: 'dateOfEnd',
      render: (date) => date ? moment(date).format('DD.MM.YYYY HH:mm') : '-',
    },
    {
      title: 'Действия',
      key: 'actions',
      width: 150,
      align: 'center',
      render: (_, record) => (
        <Space size={4}>
          <Button
            type="primary"
            size="small"
            icon={<EditOutlined />}
            onClick={() => onEdit(record)}
            style={{ background: '#b49a6a', border: 'none', padding: '0 6px' }}
            title="Редактировать"
          />
          <Popconfirm
            title="Вы уверены, что хотите удалить этот хакатон?"
            onConfirm={() => onDelete(record.id)}
            okText="Да"
            cancelText="Нет"
          >
            <Button
              type="primary"
              size="small"
              danger
              icon={<DeleteOutlined />}
              style={{ background: '#b49a6a', border: 'none', padding: '0 6px' }}
              title="Удалить"
            />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Table
      columns={columns}
      dataSource={data}
      rowKey="id"
      pagination={{ pageSize: 10 }}
    />
  );
};

export default EventsTable;