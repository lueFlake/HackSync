import ApiService from './ApiService';

class AuthService {
  static async register(userData) {
    try {
      return await ApiService.request("/auth/register", "POST", userData, false);
    } catch (error) {
      console.error("Registration failed:", error);
      throw error;
    }
  }

  static async login(credentials) {
    try {
      const data = await ApiService.request("/auth/login", "POST", credentials, false);
      localStorage.setItem("accessToken", data.accessToken);
      return data;
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    }
  }

  static async refreshToken() {
    try {
      const { accessToken } = await ApiService.request(
        "/auth/refresh",
        "POST",
        null,
        false,
        ApiService.REFRESH_TIMEOUT
      );
      localStorage.setItem("accessToken", accessToken);
      return accessToken;
    } catch (error) {
      this.logout();
      throw error;
    }
  }

  static async logout() {
    try {
      await ApiService.request("/auth/logout", "POST", null, false);
    } finally {
      localStorage.removeItem("accessToken");
    }
  }

  static async isAuthenticated() {
    const token = localStorage.getItem("accessToken");
    if (!token) return false;

    // Optional: Add token validation logic here
    // For example, check expiration if JWT
    return true;
  }
}

export default AuthService;