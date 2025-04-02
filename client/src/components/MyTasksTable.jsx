// здесь просто Ant описание страницы

import React from 'react';
import { Table, Tag } from 'antd';
import { useNavigate } from 'react-router-dom';

const statusMap = {
  todo: { text: 'К выполнению', color: 'default' },
  in_progress: { text: 'В работе', color: 'blue' },
  review: { text: 'На проверке', color: 'orange' },
  done: { text: 'Завершено', color: 'green' },
  bug: { text: 'Ошибка', color: 'red' }
};

const priorityMap = {
  low: { text: 'Низкий', color: 'green' },
  medium: { text: 'Средний', color: 'orange' },
  high: { text: 'Высокий', color: 'volcano' },
  critical: { text: 'Критичный', color: 'red' }
};

const MyTasksTable = ({ tasks }) => {
  const navigate = useNavigate();

  const columns = [
    {
      title: "Номер задачи",
      dataIndex: "id",
      key: "id",
      render: (id) => (
        <a onClick={(e) => {
          e.preventDefault();
          navigate(`/task/${id}`);
        }}>
          {id}
        </a>
      )
    },
    {
      title: "Название задачи",
      dataIndex: "name",
      key: "name",
      render: (text, record) => (
        <div>
          <span style={{ fontWeight: 'bold' }}>{text}</span>
          <div style={{ marginTop: 4, color: 'rgba(0,0,0,0.45)' }}>
            {record.description}
          </div>
        </div>
      )
    },
    {
      title: "Статус",
      dataIndex: "status",
      key: "status",
      render: (status) => (
        <Tag color={statusMap[status].color}>
          {statusMap[status].text}
        </Tag>
      )
    },
    {
      title: "Приоритет",
      dataIndex: "priority",
      key: "priority",
      render: (priority) => (
        <Tag color={priorityMap[priority].color}>
          {priorityMap[priority].text}
        </Tag>
      )
    },
    {
      title: "Срок выполнения",
      dataIndex: "deadline",
      key: "deadline",
      render: (date) => date || '-'
    }
  ];

  return (
    <Table
      rowKey="id"
      dataSource={tasks}
      columns={columns}
      pagination={false}
      bordered
      size="middle"
      onRow={(record) => ({
        onClick: () => navigate(`/task/${record.id}`),
      })}
    />
  );
};

export default MyTasksTable;