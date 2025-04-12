import React, { useState } from 'react';
import {
  DndContext,
  DragOverlay,
  closestCorners,
  useSensor,
  useSensors,
  PointerSensor,
  TouchSensor,
} from '@dnd-kit/core';
import {
  SortableContext,
  verticalListSortingStrategy,
  arrayMove,
} from '@dnd-kit/sortable';
import { Container, Column, Task } from './Kanban/KanbanComponents';
import { Card, Tag, Avatar } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import { message } from 'antd';
import { ApiService } from '../services/ApiService';

const KanbanBoard = () => {
  const [columns, setColumns] = useState([
    {
      id: 'backlog',
      title: 'Бэклог',
      tasks: [
        {
          id: '1',
          title: 'Реализация авторизации',
          author: 'Иван Иванов',
          event: 'Хакатон 2024',
        },
        {
          id: '2',
          title: 'Реализация фильтров',
          author: 'Петр Петров',
          event: 'Хакатон 2025',
        }
      ]
    },
    { id: 'todo', title: 'Сделать', tasks: [] },
    { id: 'progress', title: 'В работе', tasks: [] },
    { id: 'done', title: 'Готово', tasks: [] }
  ]);

  const [activeTask, setActiveTask] = useState(null);

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: { distance: 5 }
    }),
    useSensor(TouchSensor)
  );

  // Функция для поиска колонки по ID задачи
  const findColumn = (taskId) => {
    return columns.find(col =>
      col.tasks.some(task => task.id === taskId)
    )?.id;
  };

  // Обработчик начала перетаскивания
  const handleDragStart = ({ active }) => {
    setActiveTask(columns
      .flatMap(col => col.tasks)
      .find(task => task.id === active.id));
  };

  const handleDragOver = ({ active, over }) => {
    if (!over) return;

    const activeId = active.id;
    const overId = over.id;
    const activeCol = findColumn(activeId);
    const overCol = findColumn(overId) || over.id;

    if (!activeCol || !overCol) return;

    if (activeCol !== overCol) {
      setColumns(prev =>
        prev.map(col => {
          if (col.id === activeCol) {
            return {
              ...col,
              tasks: col.tasks.filter(t => t.id !== activeId)
            };
          }
          if (col.id === overCol) {
            return {
              ...col,
              tasks: [activeTask, ...col.tasks]
            };
          }
          return col;
        })
      );
    }
  };

  // Обработчик завершения перетаскивания
  const handleDragEnd = async ({ active, over }) => {
    if (!over) return;
    console.log(active, over);

    const taskId = active.id;
    const newStatus = over.id;
    const task = columns
      .flatMap(col => col.tasks)
      .find(task => task.id === taskId);

    // Оптимистичное обновление
    const originalColumns = [...columns];
    const updatedColumns = columns.map(col => ({
      ...col,
      tasks: col.tasks.filter(t => t.id !== taskId)
    }));

    const column = updatedColumns.find(col => col.id === newStatus);

    if (column) {
      column.tasks.push(task);
    }

    setColumns(updatedColumns);

    try {
      await ApiService.updateTask(taskId, { status: newStatus });
    } catch (err) {
      setColumns(originalColumns);
      message.error('Не удалось сохранить изменения');
    }
  };

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCorners}
      onDragStart={handleDragStart}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
    >
      <div style={{
        display: 'flex',
        gap: '16px',
        padding: '20px',
        overflowX: 'auto'
      }}>
        {columns.map(column => (
          <Column
            key={column.id}
            id={column.id}
            title={
              <div style={{
                padding: '8px',
                minHeight: '60px',
                backgroundColor: '#fff',
                borderRadius: '8px'
              }}>
                <Tag color="blue">{column.title}</Tag>
                <span style={{ marginLeft: '8px' }}>({column.tasks.length})</span>
              </div>
            }
          >
            <SortableContext
              items={column.tasks}
              strategy={verticalListSortingStrategy}
            >
              {column.tasks.map(task => (
                <Task key={task.id} id={task.id}>
                  <Card
                    size="small"
                    style={{
                      marginBottom: '8px',
                      cursor: 'grab',
                      userSelect: 'none'
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <Avatar size="small" icon={<UserOutlined />} />
                      <span>{task.author}</span>
                    </div>
                    <Tag color="blue" style={{ marginTop: '8px' }}>
                      {task.event}
                    </Tag>
                    <h4 style={{ margin: 0 }}>{task.title}</h4>
                  </Card>
                </Task>
              ))}
              {/* Плейсхолдер для пустых колонок */}
              {column.tasks.length === 0 && (
                <div style={{
                  minHeight: '100px',
                  border: '2px dashed #e8e8e8',
                  borderRadius: '8px'
                }} />
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
              width: '250px',
              boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
              transform: 'rotate(3deg)'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Avatar size="small" icon={<UserOutlined />} />
              <span>{activeTask.author}</span>
            </div>
            <Tag color="blue" style={{ marginTop: '8px' }}>
              {activeTask.event}
            </Tag>
            <h4 style={{ margin: 0 }}>{activeTask.title}</h4>
          </Card>
        )}
      </DragOverlay>
    </DndContext>
  );
};

export default KanbanBoard;