class AuthService {
  static isAuthenticated() {
    return !!localStorage.getItem('token');
  }

  static logout() {
    localStorage.removeItem('token');
    // нужно будет еще удалить refresh token из httpsOnly cookies
  }
}

export default AuthService;