import { UserOutlined } from "@ant-design/icons";
import {
  DndContext,
  DragOverlay,
  PointerSensor,
  TouchSensor,
  closestCorners,
  useSensor,
  useSensors,
} from "@dnd-kit/core";
import {
  SortableContext,
  verticalListSortingStrategy,
} from "@dnd-kit/sortable";
import { Avatar, Card, Tag, message } from "antd";
import { useEffect, useState } from "react";
import { ApiService } from "../services/ApiService";
import { Column, Task } from "./Kanban/KanbanComponents";

const KanbanBoard = () => {
  const [columns, setColumns] = useState([]);
  const [activeTask, setActiveTask] = useState(null);
  const [loading, setLoading] = useState(true);
  const [statuses, setStatuses] = useState([]);

  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 5 } }),
    useSensor(TouchSensor)
  );

  // Fetch statuses and tasks on mount
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [tasks, kanbanStatuses] = await Promise.all([
          ApiService.getTasks(),
          ApiService.getKanbanStatuses(),
        ]);

        setStatuses(kanbanStatuses);

        // Create columns from statuses
        const initialColumns = kanbanStatuses.map((status) => ({
          id: status.id,
          title: status.name,
          color: status.color,
          tasks: [],
        }));

        const updatedColumns = initialColumns.map((col) => ({
          ...col,
          tasks: tasks
            .filter((task) => task.status === col.id)
            .map((task) => ({
              id: task.id,
              number: task.number,
              title: task.name,
              author: task.userId,
              event: task.hackathonId,
              description: task.description,
              priority: task.priority,
            })),
        }));

        // Sort tasks by number in each column
        updatedColumns.forEach((col) => {
          col.tasks.sort((a, b) => a.number.localeCompare(b.number));
        });

        setColumns(updatedColumns);
      } catch (error) {
        message.error("Не удалось загрузить данные");
        console.error(error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const findColumn = (taskNumber) => {
    return columns.find((col) =>
      col.tasks.some((task) => task.number === taskNumber)
    )?.id;
  };

  const handleDragStart = ({ active }) => {
    // Check if the dragged item is a column
    const isColumn = columns.some((col) => col.id === active.id);
    if (isColumn) {
      return;
    }

    setActiveTask(
      columns
        .flatMap((col) => col.tasks)
        .find((task) => task.number === active.id)
    );
  };

  const handleDragOver = ({ active, over }) => {
    if (!over) return;

    const activeNumber = active.id;
    const overNumber = over.id;
    const activeCol = findColumn(activeNumber);
    const overCol = findColumn(overNumber) || overNumber;

    if (!activeCol || !overCol || activeCol === overCol) return;

    setColumns((prev) =>
      prev.map((col) => {
        if (col.id === activeCol) {
          return {
            ...col,
            tasks: col.tasks.filter((t) => t.number !== activeNumber),
          };
        }
        if (col.id === overCol) {
          return { ...col, tasks: [activeTask, ...col.tasks] };
        }
        return col;
      })
    );
  };

  const handleDragEnd = async ({ active, over }) => {
    if (!over) return;

    const taskNumber = active.id;
    const overNumber = over.id;
    const overCol = findColumn(overNumber) || overNumber;

    // Find the task being dragged
    const task = columns
      .flatMap((col) => col.tasks)
      .find((task) => task.number === taskNumber);

    // Find the target status from our statuses list
    const targetStatus = statuses.find((s) => s.id === overCol);
    if (!targetStatus) {
      message.error("Статус не найден");
      return;
    }

    // Optimistic update
    const originalColumns = [...columns];
    const updatedColumns = columns.map((col) => ({
      ...col,
      tasks: col.tasks.filter((t) => t.number !== taskNumber),
    }));
    const column = updatedColumns.find((col) => col.id === overCol);
    if (column) {
      column.tasks.push(task);
    }
    setColumns(updatedColumns);

    try {
      await ApiService.updateTask(task.id, {
        id: task.id,
        statusId: targetStatus.id,
      });
      message.success("Статус задачи обновлен");
    } catch (err) {
      setColumns(originalColumns);
      message.error("Не удалось сохранить изменения");
      console.error(err);
    }
    setActiveTask(null);
  };

  if (loading) {
    return <div>Загрузка...</div>;
  }

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCorners}
      onDragStart={handleDragStart}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
    >
      <div
        style={{
          display: "flex",
          gap: "16px",
          padding: "20px",
          overflowX: "auto",
        }}
      >
        {columns.map((column) => (
          <Column
            key={column.id}
            id={column.id}
            title={
              <div
                style={{
                  padding: "8px",
                  minHeight: "60px",
                  backgroundColor: "#fff",
                  borderRadius: "8px",
                }}
              >
                <Tag color={column.color}>{column.title}</Tag>
                <span style={{ marginLeft: "8px" }}>
                  ({column.tasks.length})
                </span>
              </div>
            }
          >
            <SortableContext
              items={column.tasks.map((task) => task.number)}
              strategy={verticalListSortingStrategy}
            >
              {column.tasks.map((task) => (
                <Task key={task.id} id={task.number}>
                  <Card
                    size="small"
                    style={{
                      marginBottom: "8px",
                      cursor: "grab",
                      userSelect: "none",
                    }}
                  >
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "8px",
                      }}
                    >
                      <Avatar size="small" icon={<UserOutlined />} />
                      <span>{task.author}</span>
                    </div>
                    <Tag color="blue" style={{ marginTop: "8px" }}>
                      {task.event}
                    </Tag>
                    <h4 style={{ margin: 0 }}>
                      {task.number} - {task.title}
                    </h4>
                    {task.description && (
                      <p
                        style={{ margin: "8px 0 0", color: "rgba(0,0,0,0.45)" }}
                      >
                        {task.description}
                      </p>
                    )}
                  </Card>
                </Task>
              ))}
              {column.tasks.length === 0 && (
                <div
                  style={{
                    minHeight: "100px",
                    border: "2px dashed #e8e8e8",
                    borderRadius: "8px",
                  }}
                />
              )}
            </SortableContext>
          </Column>
        ))}
      </div>

      <DragOverlay>
        {activeTask && (
          <Card
            size="small"
            style={{
              width: "250px",
              boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
              transform: "rotate(3deg)",
            }}
          >
            <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
              <Avatar size="small" icon={<UserOutlined />} />
              <span>{activeTask.author}</span>
            </div>
            <Tag color="blue" style={{ marginTop: "8px" }}>
              {activeTask.event}
            </Tag>
            <h4 style={{ margin: 0 }}>{activeTask.title}</h4>
            {activeTask.description && (
              <p style={{ margin: "8px 0 0", color: "rgba(0,0,0,0.45)" }}>
                {activeTask.description}
              </p>
            )}
          </Card>
        )}
      </DragOverlay>
    </DndContext>
  );
};

export default KanbanBoard;
