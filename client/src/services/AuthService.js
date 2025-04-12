// services/AuthService.js
import ApiService from './ApiService';

class AuthService {
  /**
   * Логин: сохраняет accessToken и устанавливает refreshToken в cookie
   */
  static async login(credentials) {
    const response = await ApiService.post('/auth/login', credentials);
    localStorage.setItem('accessToken', response.data.accessToken);
    // Refresh token автоматически сохраняется в HttpOnly cookie сервером
    return response.data;
  }

  /**
   * Обновление access token через refresh token
   */
  static async refreshToken() {
    try {
      // Отправляем запрос с refresh token (автоматически подставляется из cookies)
      const response = await ApiService.post('/auth/refresh-token');
      
      // Обновляем access token
      localStorage.setItem('accessToken', response.data.accessToken);
      return response.data.accessToken;
    } catch (error) {
      this.logout();
      throw error;
    }
  }

  /**
   * Выход: очищаем токены
   */
  static logout() {
    localStorage.removeItem('accessToken');
    // Удаляем refresh token через API (сервер очистит cookie)
    return ApiService.post('/auth/logout');
  }

  /**
   * Проверка наличия access token
   */
  static isAuthenticated() {
    return !!localStorage.getItem('accessToken');
  }
}

export default AuthService;