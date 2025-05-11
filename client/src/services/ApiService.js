// надо будет выбрать констаты для таймаутов !!!

export class ApiService {
  static _isRefreshing = false;
  static _refreshQueue = [];
  static DEFAULT_TIMEOUT = 10000;
  static REFRESH_TIMEOUT = 30000;
  static DEFAULT_CACHE_DURATION = 5 * 60 * 1000; // 5 minutes in milliseconds

  static BASE_URL = `http://localhost:8080`

  // Cache storage
  static _cache = new Map();

  /**
   * Get cached data if available and not expired
   * @param {string} key - Cache key
   * @returns {object|null} Cached data or null if not found/expired
   */
  static _getCachedData(key) {
    const cached = this._cache.get(key);
    if (!cached) return null;

    if (Date.now() > cached.expiresAt) {
      this._cache.delete(key);
      return null;
    }

    return cached.data;
  }

  /**
   * Set data in cache
   * @param {string} key - Cache key
   * @param {object} data - Data to cache
   * @param {number} duration - Cache duration in milliseconds
   */
  static _setCachedData(key, data, duration = this.DEFAULT_CACHE_DURATION) {
    this._cache.set(key, {
      data,
      expiresAt: Date.now() + duration
    });
  }

  /**
   * Clear specific cache entry or all cache
   * @param {string} [key] - Optional cache key to clear specific entry
   */
  static clearCache(key) {
    if (key) {
      this._cache.delete(key);
    } else {
      this._cache.clear();
    }
  }

  /**
   * Базовый метод запроса с таймаутом и кэшированием
   * @param {string} endpoint - API endpoint
   * @param {string} method - HTTP метод
   * @param {object|null} body - Тело запроса
   * @param {boolean} retry - Повторять при 401 ошибке
   * @param {number|null} timeoutMs - Таймаут в миллисекундах
   * @param {boolean} useCache - Использовать кэш
   * @param {number|null} cacheDuration - Длительность кэша в миллисекундах
   */
  static async request(endpoint, method = "GET", body = null, retry = true, timeoutMs = null, useCache = false, cacheDuration = null) {
    if (useCache && method === "GET") {
      const cacheKey = `${endpoint}${body ? JSON.stringify(body) : ''}`;
      const cachedData = this._getCachedData(cacheKey);
      if (cachedData) {
        return cachedData;
      }
    }

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

      const response = await fetch(`${ApiService.BASE_URL}${endpoint}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : null,
        credentials: 'include',
        signal: controller.signal
      });

      clearTimeout(timeoutId);

      if (!response.ok) {
        if (response.status === 401 && endpoint !== '/auth/refresh') {
          return this._handleUnauthorized(endpoint, method, body, timeoutMs);
        }
        throw await this._parseError(response);
      }

      const data = await response.json();

      // Cache successful GET responses
      if (useCache && method === "GET") {
        const cacheKey = `${endpoint}${body ? JSON.stringify(body) : ''}`;
        this._setCachedData(cacheKey, data, cacheDuration);
      }

      return data;
    } catch (error) {
      clearTimeout(timeoutId);
      if (error.name === 'AbortError') {
        throw new Error(`Request timeout (${timeout}ms)`);
      }
      throw error;
    }
  }


  // Private methods -------------------------------------

  static async _handleUnauthorized(endpoint, method, body, timeoutMs) {
    if (this._isRefreshing) {
      return new Promise((resolve, reject) => {
        this._refreshQueue.push({ resolve, reject });
      }).then(() => this.request(endpoint, method, body, false, timeoutMs));
    }

    this._isRefreshing = true;

    try {
      const response = await this.request(
        '/auth/refresh',
        'POST',
        null,
        false,
        this.REFRESH_TIMEOUT
      );

      if (!response.accessToken) {
        throw new Error('No access token in refresh response');
      }

      localStorage.setItem("accessToken", response.accessToken);
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
    this._refreshQueue.forEach(({ resolve }) => resolve());
    this._refreshQueue = [];
  }

  static _rejectRefreshQueue(error) {
    this._refreshQueue.forEach(({ reject }) => reject(error));
    this._refreshQueue = [];
  }

  static async _parseError(response) {
    const error = await response.json().catch(() => ({ message: 'Unknown error' }));
    return new Error(error.message || 'Request failed');
  }

  // Public methods ================================================

  // Events ----------------------------------------------------------
  static async getEvents(timeoutMs = this.DEFAULT_TIMEOUT, useCache = true) {
    return this.request("/events", "GET", null, true, timeoutMs, useCache);
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
  static async getTasks(timeoutMs = this.DEFAULT_TIMEOUT, useCache = true) {
    return this.request("/tasks", "GET", null, true, timeoutMs, useCache);
  }

  static async getTaskById(id, timeoutMs = this.DEFAULT_TIMEOUT, useCache = true) {
    return this.request(`/tasks/${id}`, "GET", null, true, timeoutMs, useCache);
  }

  static async getTasksByUserId(userId, timeoutMs = this.DEFAULT_TIMEOUT, useCache = true) {
    return this.request(`/tasks/user/${userId}`, "GET", null, true, timeoutMs, useCache);
  }

  static async getTasksByStatusId(statusId, timeoutMs = this.DEFAULT_TIMEOUT, useCache = true) {
    return this.request(`/tasks/status/${statusId}`, "GET", null, true, timeoutMs, useCache);
  }

  static async createTask(task, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request("/tasks", "POST", task, true, timeoutMs);
  }

  static async updateTask(taskId, updates, timeoutMs = this.DEFAULT_TIMEOUT) {
    // Ensure we're sending the correct format for UUIDs
    const payload = {
      ...updates,
      id: taskId,  // Keep the UUID format
      statusId: updates.statusId  // Keep the UUID format
    };
    return this.request(`/tasks/${taskId}`, "PATCH", payload, true, timeoutMs);
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


  // User ------------------------------------------------------------
  static async getUsers(timeoutMs = this.DEFAULT_TIMEOUT, useCache = true) {
    return this.request("/users/all", "GET", null, true, timeoutMs, useCache);
  }

  static async getUserById(id, timeoutMs = this.DEFAULT_TIMEOUT, useCache = true) {
    return this.request(`/users/${id}`, "GET", null, true, timeoutMs, useCache);
  }

  static async createUser(userData, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request("/users", "POST", userData, false, timeoutMs);
  }

  static async updateUser(id, updateData, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request(`/users/${id}`, "PUT", updateData, true, timeoutMs);
  }

  static async changePassword(id, passwords, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request(`/users/${id}/change-password`, "POST", passwords, true, timeoutMs);
  }

  static async deleteUser(id, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request(`/users/${id}`, "DELETE", null, true, timeoutMs);
  }

  // Status ----------------------------------------------------------

  static async getKanbanStatuses(timeoutMs = this.DEFAULT_TIMEOUT, useCache = true) {
    return this.request("/kanban-statuses", "GET", null, true, timeoutMs, useCache);
  }

  static async getKanbanStatusById(id, timeoutMs = this.DEFAULT_TIMEOUT, useCache = true) {
    return this.request(`/kanban-statuses/${id}`, "GET", null, true, timeoutMs, useCache);
  }

  static async createKanbanStatus(statusData, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request("/kanban-statuses", "POST", statusData, true, timeoutMs);
  }

  static async updateKanbanStatus(id, statusData, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request(`/kanban-statuses/${id}`, "PUT", statusData, true, timeoutMs);
  }

  static async deleteKanbanStatus(id, timeoutMs = this.DEFAULT_TIMEOUT) {
    return this.request(`/kanban-statuses/${id}`, "DELETE", null, true, timeoutMs);
  }
  // Chat ------------------------------------------------------------
  static async chatHistory(timeoutMs = this.DEFAULT_TIMEOUT, useCache = true) {
    return this.request(`/chat/history`, "GET", null, true, timeoutMs, useCache);
  }
}

export default ApiService;