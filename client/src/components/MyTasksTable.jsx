// здесь просто Ant описание страницы

import { Table, Tag } from "antd";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import ApiService from "../services/ApiService";

const priorityMap = {
  low: { text: "Низкий", color: "green" },
  medium: { text: "Средний", color: "orange" },
  high: { text: "Высокий", color: "volcano" },
  critical: { text: "Критичный", color: "red" },
};

const MyTasksTable = ({ tasks }) => {
  const navigate = useNavigate();
  const [statusMap, setStatusMap] = useState(null);

  useEffect(() => {
    const fetchStatuses = async () => {
      try {
        const statuses = await ApiService.getKanbanStatuses();
        const map = {};
        statuses.forEach((status) => {
          map[status.name] = {
            text: status.name,
            color: status.color,
          };
        });
        setStatusMap(map);
      } catch (error) {
        console.error("Error fetching statuses:", error);
      }
    };

    fetchStatuses();
  }, []);

  const columns = [
    {
      title: "Номер задачи",
      dataIndex: "id",
      key: "id",
      render: (id) => (
        <a
          onClick={(e) => {
            e.preventDefault();
            navigate(`/task/${id}`);
          }}
        >
          {id}
        </a>
      ),
    },
    {
      title: "Название задачи",
      dataIndex: "name",
      key: "name",
      render: (text, record) => (
        <div>
          <span style={{ fontWeight: "bold" }}>{text}</span>
          <div style={{ marginTop: 4, color: "rgba(0,0,0,0.45)" }}>
            {record.description}
          </div>
        </div>
      ),
    },
    {
      title: "Статус",
      dataIndex: "status",
      key: "status",
      render: (status, record) => (
        <Tag color={record.statusColor || "gray"}>{status}</Tag>
      ),
    },
    {
      title: "Приоритет",
      dataIndex: "priority",
      key: "priority",
      render: (priority) => (
        <Tag color={priorityMap[priority].color}>
          {priorityMap[priority].text}
        </Tag>
      ),
    },
    {
      title: "Срок выполнения",
      dataIndex: "deadline",
      key: "deadline",
      render: (date) => date || "-",
    },
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
