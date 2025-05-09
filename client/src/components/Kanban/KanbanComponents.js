import {
  defaultAnimateLayoutChanges,
  useSortable,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import React from 'react';

const animateLayoutChanges = (args) =>
  defaultAnimateLayoutChanges({ ...args, wasDragging: true });

export const Container = ({ children }) => (
  <div style={{ display: 'flex', gap: 16 }}>{children}</div>
);

export const Column = ({ id, title, children }) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
  } = useSortable({
    id,
    animateLayoutChanges,
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    width: 300,
    backgroundColor: '#f0f2f5',
    borderRadius: 8,
    padding: 16,
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes} {...listeners}>
      <div>{title}</div>
      {children}
    </div>
  );
};

export const Task = ({ id, children }) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
  } = useSortable({ id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes} {...listeners}>
      {children}
    </div>
  );
};