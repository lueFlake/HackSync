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
      return {
        accessToken: data.accessToken,
        userId: data.userId,
        email: data.email,
        name: data.name
      };
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

    try {
      // Use a shorter cache duration (1 minute) for token validation since it's a security check
      await ApiService.request("/auth/validate", "GET", null, false, null, true, 60 * 1000);
      return true;
    } catch (error) {
      localStorage.removeItem("accessToken");
      return false;
    }
  }

  static async getCurrentUser() {
    try {
      // Use a longer cache duration (30 minutes) for user data since it doesn't change frequently
      const data = await ApiService.request("/auth/me", "GET", null, true, null, true, 30 * 60 * 1000);
      return {
        userId: data.id,
        email: data.email,
        name: data.name,
        role: data.role,
        avatarUrl: data.avatarUrl
      };
    } catch (error) {
      console.error("Failed to fetch user data:", error);
      throw error;
    }
  }
}

export default AuthService;