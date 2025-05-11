import {
    Button,
    DatePicker,
    Divider,
    Form,
    Input,
    notification,
    Select,
    Spin,
} from "antd";
import { useEffect, useState } from "react";
import MyTasksTable from "../components/MyTasksTable";
import PageContainer from "../components/PageContainer";
import { useAuth } from "../contexts/AuthContext";
import { useSelectedHackathon } from '../hooks/useSelectedHackathon';
import { ApiService } from "../services/ApiService";

const { Option } = Select;

const MyTasksPage = () => {
  const [tasks, setTasks] = useState([]);
  const [kanbanStatuses, setKanbanStatuses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isCreatingTask, setIsCreatingTask] = useState(false);
  const [form] = Form.useForm();
  const { user, isLoading, isAuthenticated } = useAuth();
  const { selectedHackathon } = useSelectedHackathon();

  useEffect(() => {
    const fetchTasksAndStatuses = async () => {
      try {
        setLoading(true);
        const [tasksResponse, statusesResponse] = await Promise.all([
          user.userId
            ? ApiService.getTasksByUserId(user.userId)
            : ApiService.getTasks(),
          ApiService.getKanbanStatuses(),
        ]);

        setKanbanStatuses(statusesResponse);

        const statusMap = new Map();
        statusesResponse.forEach((s) =>
          statusMap.set(s.id, { name: s.name, color: s.color })
        );

        const mapped = tasksResponse.map((task) => {
          const s = statusMap.get(task.status);
          return {
            id: task.number.toString(),
            name: task.name,
            description: task.description,
            status: s?.name || "Неизвестно",
            statusColor: s?.color || "gray",
            priority: task.priority?.toLowerCase(),
            deadline: task.dueDate
              ? new Date(task.dueDate).toLocaleDateString()
              : "-",
          };
        });

        setTasks(mapped);
      } catch (e) {
        notification.error({ message: "Ошибка при загрузке задач" });
      } finally {
        setLoading(false);
      }
    };

    fetchTasksAndStatuses();
  }, []);

  const handleCreateStart = () => setIsCreatingTask(true);
  const handleCancel = () => {
    form.resetFields();
    setIsCreatingTask(false);
  };

  const handleSubmit = async () => {
    try {
      console.log(user);
      const values = await form.validateFields();

      const newTaskPayload = {
        name: values.name,
        description: values.description,
        priority: values.priority,
        userId: user.userId,
        hackathonId: null, // or provide actual event ID if applicable
        dueDate: values.dueDate?.toISOString(),
      };

      const savedTask = await ApiService.createTask(newTaskPayload);

      // resolve status to display name + color
      const statusMeta = kanbanStatuses.find((s) => s.id === savedTask.status);
      setTasks([
        ...tasks,
        {
          id: savedTask.id.toString(),
          name: savedTask.name,
          description: savedTask.description,
          status: statusMeta?.name || "Неизвестно",
          statusColor: statusMeta?.color || "gray",
          priority: savedTask.priority?.toLowerCase(),
          deadline: savedTask.dueDate
            ? new Date(savedTask.dueDate).toLocaleDateString()
            : "-",
        },
      ]);

      notification.success({ message: "Задача создана!" });
      handleCancel();
    } catch (err) {
      console.error(err);
      notification.error({ message: "Ошибка создания задачи" });
    }
  };

  return (
    <PageContainer title={selectedHackathon ? `Мои задачи - ${selectedHackathon.name}` : "Мои задачи"}>
      <Divider orientation="left" />
      <Button
        type="primary"
        onClick={handleCreateStart}
        disabled={isCreatingTask}
      >
        Создать задачу
      </Button>

      {isCreatingTask && (
        <>
          <Divider orientation="left">Новая задача</Divider>
          <Form layout="vertical" form={form}>
            <Form.Item
              name="name"
              label="Название"
              rules={[{ required: true }]}
            >
              <Input placeholder="Добавить логин через Telegram" />
            </Form.Item>
            <Form.Item name="description" label="Описание">
              <Input.TextArea rows={3} />
            </Form.Item>
            <Form.Item
              name="priority"
              label="Приоритет"
              rules={[{ required: true }]}
            >
              <Select>
                <Option value="LOW">Низкий</Option>
                <Option value="MEDIUM">Средний</Option>
                <Option value="HIGH">Высокий</Option>
              </Select>
            </Form.Item>
            <Form.Item name="dueDate" label="Срок выполнения">
              <DatePicker style={{ width: "100%" }} />
            </Form.Item>
            <Form.Item>
              <Button type="primary" onClick={handleSubmit}>
                Сохранить
              </Button>
              <Button onClick={handleCancel}>Отмена</Button>
            </Form.Item>
          </Form>
        </>
      )}

      <Divider />

      {loading ? (
        <Spin tip="Загрузка задач..." />
      ) : (
        <MyTasksTable tasks={tasks} />
      )}
    </PageContainer>
  );
};

export default MyTasksPage;
