export class ApiService {
  static async request(endpoint, method = "GET", body = null) {
    const response = await fetch(`/api${endpoint}`, {
      method,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
      body: body ? JSON.stringify(body) : null,
    });

    if (!response.ok) throw new Error("API Error");
    return response.json();
  }

  static async getEvents() {
    return this.request("/events");
  }

  static async createEvent(data) {
    return this.request("/events", "POST", data);
  }

  static async updateEvent(id, data) {
    return this.request(`/events/${id}`, "PUT", data);
  }

  static async deleteEvent(id) {
    return this.request(`/events/${id}`, "DELETE");
  }

  static async updateTask(taskId, updates) {
    return this.request(`/tasks/${taskId}`, "PATCH");
  }

  static async createTask(task) {
    return this.request(`/tasks`, "POST", task);
  }

  static async deleteTask(taskId) {
    return this.request(`/tasks/${taskId}`, "PATCH")
  }
}
