import {
  ArrowLeftOutlined,
  ClockCircleOutlined,
  TeamOutlined,
  UserAddOutlined,
} from "@ant-design/icons";
import {
  Button,
  Card,
  Descriptions,
  Form,
  message,
  Modal,
  Space,
  Spin,
  Table,
} from "antd";
import moment from "moment";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import DeadlineForm from "../components/forms/DeadlineForm";
import TeamForm from "../components/forms/TeamForm";
import PageContainer from "../components/PageContainer";
import { useAuth } from "../contexts/AuthContext";
import { useApi } from "../hooks/useApi";
import { useSelectedHackathon } from "../hooks/useSelectedEvent";

const TeamMembersCell = ({ teamId }) => {
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const api = useApi();

  useEffect(() => {
    const loadMembers = async () => {
      try {
        const response = await api.get(`/teams/${teamId}/members`);
        console.log(response);
        setMembers(response.data);
      } catch (error) {
        //console.error('Failed to load team members:', error);
        setMembers([]); // Set empty array on error
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

const TeamActionsCell = ({ record, onEdit, onDelete, onJoin }) => {
  const [isMember, setIsMember] = useState(false);
  const [loading, setLoading] = useState(true);
  const api = useApi();
  const { user } = useAuth();

  useEffect(() => {
    const checkMembership = async () => {
      try {
        const response = await api.get(`/teams/${record.id}/members`);
        setIsMember(response.data.some((member) => member.id === user?.userId));
      } catch (error) {
        //console.error('Failed to check team membership:', error);
      } finally {
        setLoading(false);
      }
    };
    checkMembership();
  }, [record.id, user?.userId]);

  if (loading) {
    return <Spin size="small" />;
  }

  return (
    <Space>
      {!isMember && (
        <Button
          type="link"
          icon={<UserAddOutlined />}
          onClick={() => onJoin(record.id)}
        >
          Присоединиться
        </Button>
      )}
      {isMember && (
        <Button type="link" disabled>
          Вы уже в команде
        </Button>
      )}
      <Button type="link" onClick={() => onEdit(record)}>
        Редактировать
      </Button>
      <Button type="link" onClick={() => onDelete(record.id)} danger>
        Удалить
      </Button>
    </Space>
  );
};

const EventDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [teams, setTeams] = useState([]);
  const [deadlines, setDeadlines] = useState([]);
  const [isCreatingTeam, setIsCreatingTeam] = useState(false);
  const [isCreatingDeadline, setIsCreatingDeadline] = useState(false);
  const [editingTeam, setEditingTeam] = useState(null);
  const [editingDeadline, setEditingDeadline] = useState(null);
  const [teamForm] = Form.useForm();
  const [deadlineForm] = Form.useForm();
  const api = useApi();
  const { user } = useAuth();
  const { selectedHackathon, updateSelectedTeamId } = useSelectedHackathon();

  useEffect(() => {
    loadEventDetails();
    loadTeams();
    loadDeadlines();
  }, [id]);

  const loadEventDetails = async () => {
    try {
      const response = await api.get(`/hackathons/${id}`);
      setEvent(response.data);
    } catch (error) {
      //message.error('Failed to load event details');
      navigate("/events");
    }
  };

  const loadTeams = async () => {
    try {
      const response = await api.get(`/teams?hackathonId=${id}`);
      setTeams(response.data);
    } catch (error) {
      //message.error('Failed to load teams');
    }
  };

  const loadDeadlines = async () => {
    try {
      const response = await api.get(`/deadlines?hackathonId=${id}`);
      setDeadlines(response.data);
    } catch (error) {
      //message.error('Failed to load deadlines');
    }
  };

  const handleTeamSubmit = async (values) => {
    try {
      const { members, ...teamData } = values;
      let teamId;

      if (editingTeam) {
        await api.put(`/teams/${editingTeam.id}`, {
          ...teamData,
          id: editingTeam.id,
        });
        teamId = editingTeam.id;
        message.success("Команда успешно обновлена");
      } else {
        const response = await api.post("/teams", teamData);
        teamId = response.data.id;
        message.success("Команда успешно создана");
      }

      // Handle team members
      if (editingTeam) {
        // Get current members
        const currentMembers = await api.get(`/teams/${teamId}/members`);
        const currentMemberIds = currentMembers.data.map((m) => m.id);

        // Remove members that are no longer in the team
        for (const memberId of currentMemberIds) {
          if (memberId && !members.includes(memberId)) {
            try {
              await api.delete(`/teams/${teamId}/users/${memberId}`);
            } catch (error) {
              // Show more informative error
              message.error(`Ошибка при удалении участника: ${error.message}`);
              console.error("Member removal error:", error);
            }
          }
        }
      }

      // Add new members
      for (const memberId of members) {
        try {
          // Set the first member (creator) as captain, others as participants
          const role = memberId === members[0] ? "CAPTAIN" : "PARTICIPANT";
          await api.post(`/teams/${teamId}/users/${memberId}`, { role });
        } catch (error) {
          message.error(`Ошибка при добавлении участника: ${error.message}`);
          //console.error(`Failed to add member ${memberId} to team:`, error);
        }
      }

      setIsCreatingTeam(false);
      setEditingTeam(null);
      teamForm.resetFields();
      loadTeams();
    } catch (error) {
      // Show more informative error if available
      if (error && error.message) {
        message.error(error.message);
      } else {
        //message.error(editingTeam ? 'Не удалось обновить команду' : 'Не удалось создать команду');
      }
      console.log(error);
    }
  };

  const handleDeadlineSubmit = async (values) => {
    try {
      const deadlineData = {
        ...values,
        date: values.date?.toISOString(),
        type: values.type || "PROJECT",
      };

      if (editingDeadline) {
        await api.put(`/deadlines/${editingDeadline.id}`, {
          ...deadlineData,
          id: editingDeadline.id,
        });
        message.success("Deadline updated successfully");
      } else {
        await api.post("/deadlines", { ...deadlineData, hackathonId: id });
        message.success("Deadline created successfully");
      }
      setIsCreatingDeadline(false);
      setEditingDeadline(null);
      deadlineForm.resetFields();
      loadDeadlines();
    } catch (error) {
      //message.error(editingDeadline ? 'Failed to update deadline' : 'Failed to create deadline');
    }
  };

  const handleDeleteTeam = async (teamId) => {
    try {
      await api.delete(`/teams/${teamId}`, { skipJsonParse: true });
      message.success("Команда успешно удалена");
      loadTeams();
    } catch (error) {
      console.log(error);
      //message.error('Не удалось удалить команду');
    }
  };

  const handleDeleteDeadline = async (deadlineId) => {
    try {
      await api.delete(`/deadlines/${deadlineId}`);
      message.success("Deadline deleted successfully");
      loadDeadlines();
    } catch (error) {
      //message.error('Failed to delete deadline');
    }
  };

  const handleEditTeam = (team) => {
    setEditingTeam(team);
    setIsCreatingTeam(true);
    teamForm.setFieldsValue({
      name: team.name,
      members: team.members?.map((m) => m.id) || [],
    });
  };

  const handleEditDeadline = (deadline) => {
    setEditingDeadline(deadline);
    setIsCreatingDeadline(true);
    deadlineForm.setFieldsValue({
      name: deadline.name,
      date: deadline.date ? moment(deadline.date) : null,
      type: deadline.type || "PROJECT",
    });
  };

  const handleCancelTeam = () => {
    setIsCreatingTeam(false);
    setEditingTeam(null);
    teamForm.resetFields();
  };

  const handleCancelDeadline = () => {
    setIsCreatingDeadline(false);
    setEditingDeadline(null);
    deadlineForm.resetFields();
  };

  const handleJoinTeam = async (teamId) => {
    if (!user?.userId) {
      message.error('Не удалось определить текущего пользователя');
      return;
    }
    try {
      message.success('Команда выбрана');
      updateSelectedTeamId(teamId);
      loadTeams();
    } catch (error) {
      message.error('Не удалось выбрать команду');
    }
  };

  const teamColumns = [
    {
      title: "Название",
      dataIndex: "name",
      key: "name",
    },
    {
      title: "Участники",
      key: "members",
      render: (_, record) => <TeamMembersCell teamId={record.id} />,
    },
    {
      title: "Создано",
      dataIndex: "createdAt",
      key: "createdAt",
      render: (date) => moment(date).format("DD.MM.YYYY HH:mm"),
    },
    {
      title: "Действия",
      key: "actions",
      render: (_, record) => (
        <TeamActionsCell
          record={record}
          onEdit={handleEditTeam}
          onDelete={handleDeleteTeam}
          onJoin={handleJoinTeam}
        />
      ),
    },
  ];

  const deadlineColumns = [
    {
      title: "Name",
      dataIndex: "name",
      key: "name",
    },
    {
      title: "Type",
      dataIndex: "type",
      key: "type",
    },
    {
      title: "Due Date",
      dataIndex: "date",
      key: "date",
      render: (date) => moment(date).format("DD.MM.YYYY HH:mm"),
    },
    {
      title: "Actions",
      key: "actions",
      render: (_, record) => (
        <Space>
          <Button type="link" onClick={() => handleEditDeadline(record)}>
            Edit
          </Button>
          <Button
            type="link"
            onClick={() => handleDeleteDeadline(record.id)}
            danger
          >
            Delete
          </Button>
        </Space>
      ),
    },
  ];

  if (!event) {
    return null;
  }

  return (
    <PageContainer title="Event Details">
      <Space direction="vertical" size="large" style={{ width: "100%" }}>
        <Space>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate("/")}>
            Назад к событиям
          </Button>
        </Space>

        {event && (
          <Card>
            <Descriptions title="Детали события" bordered>
              <Descriptions.Item label="Название">
                {event.name}
              </Descriptions.Item>
              <Descriptions.Item label="Дата начала">
                {moment(event.startDate).format("DD.MM.YYYY HH:mm")}
              </Descriptions.Item>
              <Descriptions.Item label="Дата окончания">
                {moment(event.endDate).format("DD.MM.YYYY HH:mm")}
              </Descriptions.Item>
              <Descriptions.Item label="Описание" span={3}>
                {event.description}
              </Descriptions.Item>
            </Descriptions>
          </Card>
        )}

        {/* Teams Section */}
        <Card
          title={
            <Space>
              <TeamOutlined />
              Команды
            </Space>
          }
          extra={
            <Button
              type="primary"
              onClick={() => {
                setEditingTeam(null);
                setIsCreatingTeam(true);
              }}
            >
              Добавить команду
            </Button>
          }
        >
          <Table
            columns={teamColumns}
            dataSource={teams}
            rowKey="id"
            pagination={false}
          />
        </Card>

        {/* Deadlines Section */}
        <Card
          title={
            <Space>
              <ClockCircleOutlined />
              Дедлайны
            </Space>
          }
          extra={
            <Button
              type="primary"
              onClick={() => {
                setEditingDeadline(null);
                setIsCreatingDeadline(true);
              }}
            >
              Добавить дедлайн
            </Button>
          }
        >
          <Table
            columns={deadlineColumns}
            dataSource={deadlines}
            rowKey="id"
            pagination={false}
          />
        </Card>
      </Space>

      {/* Team Creation/Edit Modal */}
      <Modal
        title={editingTeam ? "Редактировать команду" : "Создать команду"}
        open={isCreatingTeam}
        onCancel={handleCancelTeam}
        footer={null}
      >
        <TeamForm
          form={teamForm}
          onSubmit={handleTeamSubmit}
          onCancel={handleCancelTeam}
          isEditing={!!editingTeam}
        />
      </Modal>

      {/* Deadline Creation/Edit Modal */}
      <Modal
        title={editingDeadline ? "Редактировать дедлайн" : "Создать дедлайн"}
        open={isCreatingDeadline}
        onCancel={handleCancelDeadline}
        footer={null}
      >
        <DeadlineForm
          form={deadlineForm}
          onSubmit={handleDeadlineSubmit}
          onCancel={handleCancelDeadline}
          isEditing={!!editingDeadline}
        />
      </Modal>
    </PageContainer>
  );
};

export default EventDetailsPage;
