// надо будет выбрать констаты для таймаутов !!!

export class ApiService {
  static _isRefreshing = false;
  static _refreshQueue = [];
  static _DEFAULT_TIMEOUT = 10000;
  // пока везде поставлю _TMP_TIMEOUT
  static _TMP_TIMEOUT = 100000;

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
    // Таймауты учитываются при обработке 401 ошибки
    const timeout = timeoutMs ?? this._DEFAULT_TIMEOUT;
    const timeoutId = setTimeout(() => controller.abort(), timeout);

    try {
      const response = await fetch(`/api${endpoint}`, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        },
        body: body ? JSON.stringify(body) : null,
        credentials: 'include',
        signal: controller.signal
      });

      clearTimeout(timeoutId);

      if (!response.ok) {
        // При получении 401, если это не запрос токена, необходимо обновить access token
        if (response.status === 401 && endpoint !== '/auth/refresh' && retry) {
          return this._handleUnauthorized(endpoint, method, body, timeoutMs);
        }
        throw new Error(await this._parseError(response));
      }

      return response.json();
    } catch (error) {
      clearTimeout(timeoutId);
      if (error.name === 'AbortError') {
        throw new Error(`Request timeout (${timeout}ms)`);
      }
      throw error;
    }
  }

  // Внутренние методы ================================================

  static async _handleUnauthorized(endpoint, method, body, timeoutMs) {
    if (this._isRefreshing) {
      return new Promise((resolve, reject) => {
        this._refreshQueue.push({ resolve, reject });
      }).then(() => this.request(endpoint, method, body, false, timeoutMs));
    }

    this._isRefreshing = true;

    try {
      const { accessToken } = await this.request('/auth/refresh', 'POST', null, false, timeoutMs);
      localStorage.setItem("accessToken", accessToken);
      this._flushRefreshQueue();
      return this.request(endpoint, method, body, false, timeoutMs);
    } catch (error) {
      this._rejectRefreshQueue(error);
      localStorage.removeItem("accessToken");
      window.location.href = '/login?reason=session_expired';
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
      return errorData.message || `HTTP error ${response.status}`;
    } catch {
      return `HTTP error ${response.status}`;
    }
  }

  // Публичные методы ================================================

  // Events ----------------------------------------------------------
  static async getEvents(timeoutMs = this._TMP_TIMEOUT) {
    return this.request("/events", "GET", null, true, timeoutMs);
  }

  static async createEvent(data, timeoutMs = this._TMP_TIMEOUT) {
    return this.request("/events", "POST", data, true, timeoutMs);
  }

  static async updateEvent(id, data, timeoutMs = this._TMP_TIMEOUT) {
    return this.request(`/events/${id}`, "PUT", data, true, timeoutMs);
  }

  static async deleteEvent(id, timeoutMs = this._TMP_TIMEOUT) {
    return this.request(`/events/${id}`, "DELETE", null, true, timeoutMs);
  }

  // Tasks -----------------------------------------------------------
  static async getTasks(timeoutMs = this._TMP_TIMEOUT) {
    return this.request("/tasks", "GET", null, true, timeoutMs);
  }

  static async createTask(task, timeoutMs = this._TMP_TIMEOUT) {
    return this.request("/tasks", "POST", task, true, timeoutMs);
  }

  static async updateTask(taskId, updates, timeoutMs = this._TMP_TIMEOUT) {
    return this.request(`/tasks/${taskId}`, "PATCH", updates, true, timeoutMs);
  }

  static async deleteTask(taskId, timeoutMs = this._TMP_TIMEOUT) {
    return this.request(`/tasks/${taskId}`, "DELETE", null, true, timeoutMs);
  }

  // Auth ------------------------------------------------------------
  static async login(credentials, timeoutMs = this._TMP_TIMEOUT) {
    const data = await this.request("/auth/login", "POST", credentials, false, timeoutMs);
    localStorage.setItem("accessToken", data.accessToken);
    return data;
  }

  static async logout(timeoutMs = this._TMP_TIMEOUT) {
    await this.request("/auth/logout", "POST", null, false, timeoutMs);
    localStorage.removeItem("accessToken");
  }
}

export default ApiService;