import {
  Button,
  DatePicker,
  Divider,
  Form,
  Input,
  message,
  Select,
  Space,
} from "antd";
import moment from "moment";
import { useEffect, useState } from "react";
import EventsTable from "../components/EventsTable";
import PageContainer from "../components/PageContainer";
import { useApi } from "../hooks/useApi";
import { useSelectedHackathon } from "../hooks/useSelectedHackathon";
const { TextArea } = Input;
const { Option } = Select;

const EventsPage = () => {
  const [form] = Form.useForm();
  const [editingId, setEditingId] = useState(null);
  const [events, setEvents] = useState([]);
  const [isCreating, setIsCreating] = useState(false);
  const api = useApi();
  const { selectedHackathon } = useSelectedHackathon();

  useEffect(() => {
    loadHackathons();
  }, []);

  const loadHackathons = async () => {
    try {
      const response = await api.get("/hackathons/all");
      setEvents(response.data);
    } catch (error) {
      message.error("Failed to load hackathons");
    }
  };

  const handleSubmit = async (values) => {
    try {
      const data = {
        ...values,
        dateOfRegister: values.dateOfRegister?.toISOString(),
        dateOfStart: values.dateOfStart?.toISOString(),
        dateOfEnd: values.dateOfEnd?.toISOString(),
      };

      if (editingId) {
        await api.put(`/hackathons/${editingId}`, { ...data, id: editingId });
        message.success("Hackathon updated successfully");
      } else {
        await api.post("/hackathons", data);
        message.success("Hackathon created successfully");
      }

      form.resetFields();
      setEditingId(null);
      setIsCreating(false);
      loadHackathons();
    } catch (error) {
      message.error("Failed to save hackathon");
    }
  };

  const handleDelete = async (id) => {
    try {
      await api.delete(`/hackathons/${id}`);
      message.success("Hackathon deleted successfully");
      loadHackathons();
    } catch (error) {
      message.error("Failed to delete hackathon");
    }
  };

  const handleCreateStart = () => setIsCreating(true);

  const handleCancel = () => {
    form.resetFields();
    setIsCreating(false);
    setEditingId(null);
  };

  const handleEdit = (record) => {
    setEditingId(record.id);
    setIsCreating(true);
    form.setFieldsValue({
      ...record,
      dateOfRegister: record.dateOfRegister
        ? moment(record.dateOfRegister)
        : null,
      dateOfStart: record.dateOfStart ? moment(record.dateOfStart) : null,
      dateOfEnd: record.dateOfEnd ? moment(record.dateOfEnd) : null,
    });
  };

  return (
    <PageContainer
      title={
        selectedHackathon ? `События - ${selectedHackathon.name}` : "События"
      }
    >
      <Divider orientation="left"></Divider>
      <Button type="primary" onClick={handleCreateStart} disabled={isCreating}>
        Создать событие
      </Button>

      {isCreating && (
        <>
          <Divider orientation="left">
            {editingId ? "Редактировать событие" : "Новое событие"}
          </Divider>
          <Form
            form={form}
            onFinish={handleSubmit}
            layout="vertical"
            style={{ maxWidth: "600px", marginBottom: "24px" }}
          >
            <Form.Item
              name="name"
              label="Название"
              rules={[
                {
                  required: true,
                  message: "Пожалуйста, введите название хакатона",
                },
              ]}
            >
              <Input />
            </Form.Item>

            <Form.Item
              name="description"
              label="Описание"
              rules={[
                {
                  required: true,
                  message: "Пожалуйста, введите описание хакатона",
                },
              ]}
            >
              <TextArea rows={4} />
            </Form.Item>

            <Form.Item
              name="dateOfRegister"
              label="Дата регистрации"
              rules={[
                {
                  required: true,
                  message: "Пожалуйста, выберите дату регистрации",
                },
              ]}
            >
              <DatePicker showTime style={{ width: "100%" }} />
            </Form.Item>

            <Form.Item
              name="dateOfStart"
              label="Дата начала"
              rules={[
                { required: true, message: "Пожалуйста, выберите дату начала" },
              ]}
            >
              <DatePicker showTime style={{ width: "100%" }} />
            </Form.Item>

            <Form.Item
              name="dateOfEnd"
              label="Дата окончания"
              rules={[
                {
                  required: true,
                  message: "Пожалуйста, выберите дату окончания",
                },
              ]}
            >
              <DatePicker showTime style={{ width: "100%" }} />
            </Form.Item>

            {/*<Form.Item
              name="deadlines"
              label="Дедлайны"
            >
              <Select
                mode="multiple"
                placeholder="Выберите дедлайны"
                style={{ width: '100%' }}
              >
                {deadlines.map(deadline => (
                  <Option key={deadline.id} value={deadline.id}>
                    {deadline.name}
                  </Option>
                ))}
              </Select>
            </Form.Item>*/}

            <Form.Item>
              <Space>
                <Button type="primary" htmlType="submit">
                  {editingId ? "Сохранить" : "Создать"}
                </Button>
                <Button onClick={handleCancel}>Отмена</Button>
              </Space>
            </Form.Item>
          </Form>
        </>
      )}
      <Divider />

      <EventsTable data={events} onEdit={handleEdit} onDelete={handleDelete} />
    </PageContainer>
  );
};

export default EventsPage;
