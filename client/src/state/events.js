import { atom, selectorFamily, useRecoilState, useRecoilValue } from 'recoil';
import { ApiService } from '../services/api';

// Атом для хранения списка событий
export const eventsState = atom({
  key: 'eventsState',
  default: [],
});

// Атом для модального окна
export const modalState = atom({
  key: 'modalState',
  default: {
    isOpen: false,
    type: null, // 'create' | 'edit'
    event: null,
  },
});

// Асинхронный селектор для работы с API
export const eventQuery = selectorFamily({
  key: 'eventQuery',
  get: (id) => async () => {
    if (!id) return null;
    return await ApiService.get(`/events/${id}`);
  },
});