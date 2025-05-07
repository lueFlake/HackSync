// надо будет выбрать констаты для таймаутов !!!

export class ApiService {
  static _isRefreshing = false;
  static _refreshQueue = [];
  static DEFAULT_TIMEOUT = 10000;
  static REFRESH_TIMEOUT = 30000;

  /**
   * Базовый метод запроса с таймаутом
   * @param {string} endpoint - API endpoint
   * @param {string} method - HTTP метод
   * @param {object|null} body - Тело запроса
   * @param {boolean} retry - Повторять при 401 ошибке
   * @param {number|null} timeoutMs - Таймаут в миллисекундах
   */
  static async request(endpoint, method = "GET", body = null, retry = true, timeoutMs = null) {
    const controller = new AbortController();
    const timeout = timeoutMs ?? this.DEFAULT_TIMEOUT;
    const timeoutId = setTimeout(() => controller.abort(), timeout);

    try {
      const headers = {
        "Content-Type": "application/json",
      };

      const accessToken = localStorage.getItem("accessToken");
      if (accessToken) {
        headers.Authorization = `Bearer ${accessToken}`;
      }

      const response = await fetch(`http://localhost:8080${endpoint}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : null,
        credentials: 'include',
        signal: controller.signal
      });

      clearTimeout(timeoutId);

      if (!response.ok) {
        if (response.status === 401 && endpoint !== '/auth/refresh' && retry) {
          return this._handleUnauthorized(endpoint, method, body, timeoutMs);
        }
        throw await this._parseError(response);
      }

      return await response.json();
    } catch (error) {
      clearTimeout(timeoutId);
      if (error.name === 'AbortError') {
        throw new Error(`Request timeout (${timeout}ms)`);
      }
      throw error;
    }
  }


  // Внутренние методы -------------------------------------

  static async _handleUnauthorized(endpoint, method, body, timeoutMs) {
    if (this._isRefreshing) {
      return new Promise((resolve, reject) => {
        this._refreshQueue.push({ resolve, reject });
      }).then(() => this.request(endpoint, method, body, false, timeoutMs));
    }

    this._isRefreshing = true;

    try {
      const { accessToken } = await this.request(
        '/auth/refresh',
        'POST',
        null,
        false,
        this.REFRESH_TIMEOUT
      );

      localStorage.setItem("accessToken", accessToken);
      this._flushRefreshQueue();
      return this.request(endpoint, method, body, false, timeoutMs);
    } catch (error) {
      this._rejectRefreshQueue(error);
      localStorage.removeItem("accessToken");
      throw error;
    } finally {
      this._isRefreshing = false;
    }
  }

  static _flushRefreshQueue() {
    this._refreshQueue.forEach(p => p.resolve());
    this._refreshQueue = [];
  }

  static _rejectRefreshQueue(error) {
    this._refreshQueue.forEach(p => p.reject(error));
    this._refreshQueue = [];
  }

  static async _parseError(response) {
    try {
      const errorData = await response.json();
      return new Error(errorData.message || `HTTP error ${response.status}`);
    } catch {
      return new Error(`HTTP error ${response.status}`);
    }
  }

  // Public methods ================================================

  // Events ----------------------------------------------------------
  static async getEvents(timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request("/events", "GET", null, true, timeoutMs);
  }

  static async createEvent(data, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request("/events", "POST", data, true, timeoutMs);
  }

  static async updateEvent(id, data, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request(`/events/${id}`, "PUT", data, true, timeoutMs);
  }

  static async deleteEvent(id, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request(`/events/${id}`, "DELETE", null, true, timeoutMs);
  }

  // Tasks -----------------------------------------------------------
  static async getTasks(timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request("/tasks", "GET", null, true, timeoutMs);
  }

  static async createTask(task, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request("/tasks", "POST", task, true, timeoutMs);
  }

  static async updateTask(taskId, updates, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request(`/tasks/${taskId}`, "PATCH", updates, true, timeoutMs);
  }

  static async deleteTask(taskId, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request(`/tasks/${taskId}`, "DELETE", null, true, timeoutMs);
  }

  // Auth ------------------------------------------------------------
  static async register(userData, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request("/auth/register", "POST", userData, false, timeoutMs);
  }

  static async login(credentials, timeoutMs = this.DEFAULT_TIMEOUT) {
    const data = await this.request("/auth/login", "POST", credentials, false, timeoutMs);
    localStorage.setItem("accessToken", data.accessToken);
    return data;
  }

  static async logout(timeoutMs = this.DEFAULT_TIMEOUT) {
    await this.request("/auth/logout", "POST", null, false, timeoutMs);
    localStorage.removeItem("accessToken");
  }
}

export default ApiService;