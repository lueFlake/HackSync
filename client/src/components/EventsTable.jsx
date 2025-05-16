import { CheckOutlined, DeleteOutlined, EditOutlined, UserAddOutlined } from '@ant-design/icons';
import { Button, message, Modal, Popconfirm, Space, Spin, Table } from 'antd';
import moment from 'moment';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useApi } from '../hooks/useApi';
import { useSelectedHackathon } from '../hooks/useSelectedEvent';
import '../styles/EventsTable.css';

const TeamMembersCell = ({ teamId }) => {
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const api = useApi();

  useEffect(() => {
    const loadMembers = async () => {
      try {
        const response = await api.get(`/teams/${teamId}/members`);
        setMembers(response.data);
      } catch (error) {
        setMembers([]);
      } finally {
        setLoading(false);
      }
    };
    loadMembers();
  }, [teamId]);

  if (loading) {
    return <Spin size="small" />;
  }

  if (members.length === 0) {
    return <span>Нет участников</span>;
  }

  return (
    <Space direction="vertical" size="small">
      {members.map((member) => (
        <div key={`${teamId}-${member.id}`}>
          {member.name} ({member.email})
        </div>
      ))}
    </Space>
  );
};

const EventsTable = ({ data, onEdit, onDelete }) => {
  const navigate = useNavigate();
  const { selectedHackathon, selectedTeamId, setSelectedHackathonId, updateSelectedTeamId } = useSelectedHackathon();
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const api = useApi();
  const { user } = useAuth();

  const handleSelect = (id) => {
    setSelectedHackathonId(id);
    setIsModalVisible(true);
    loadTeams(id);
  };

  const loadTeams = async (hackathonId) => {
    try {
      setLoading(true);
      const response = await api.get(`/teams?hackathonId=${hackathonId}`);
      setTeams(response.data);
    } catch (error) {
      message.error('Не удалось загрузить список команд');
    } finally {
      setLoading(false);
    }
  };

  const handleJoinTeam = async (teamId) => {
    if (!user?.userId) {
      message.error('Не удалось определить текущего пользователя');
      return;
    }
    try {
      message.success('Команда выбрана');
      updateSelectedTeamId(teamId);
      loadTeams(selectedHackathon.id);
    } catch (error) {
      message.error('Не удалось выбрать команду');
    }
  };

  const teamColumns = [
    {
      title: 'Название',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Участники',
      key: 'members',
      render: (_, record) => <TeamMembersCell teamId={record.id} />,
    },
    {
      title: 'Действия',
      key: 'actions',
      render: (_, record) => (
        <Button
          type="primary"
          icon={<UserAddOutlined />}
          onClick={() => handleJoinTeam(record.id)}
          disabled={record.members?.some(m => m.id === user?.userId)}
        >
          {record.members?.some(m => m.id === user?.userId)
            ? 'Вы уже в команде'
            : 'Присоединиться'}
        </Button>
      ),
    },
  ];

  const columns = [
    {
      title: 'Название',
      dataIndex: 'name',
      key: 'name',
      render: (text, record) => (
        <a onClick={() => navigate(`/events/${record.id}`)}>{text}</a>
      ),
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
      width: 200,
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
          <Button
            type="primary"
            size="small"
            icon={<CheckOutlined />}
            onClick={() => handleSelect(record.id)}
            style={{
              background: selectedHackathon?.id === record.id ? '#52c41a' : '#b49a6a',
              border: 'none',
              padding: '0 6px'
            }}
            title="Выбрать"
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
    <>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        pagination={{ pageSize: 10 }}
        rowClassName={(record) => record.id === selectedHackathon?.id ? 'selected-row' : ''}
      />
      <Modal
        title={`Выберите команду для хакатона "${selectedHackathon?.name}"`}
        open={isModalVisible}
        onCancel={() => setIsModalVisible(false)}
        footer={null}
        width={800}
      >
        {loading ? (
          <div style={{ textAlign: 'center', padding: '20px' }}>
            <Spin size="large" />
          </div>
        ) : (
          <Table
            columns={teamColumns}
            dataSource={teams}
            rowKey="id"
            pagination={false}
          />
        )}
      </Modal>
    </>
  );
};

export default EventsTable;