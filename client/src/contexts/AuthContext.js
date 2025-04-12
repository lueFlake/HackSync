import { createContext, useContext, useState, useEffect } from 'react';
import AuthService from '../services/AuthService';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Проверяем аутентификацию при монтировании
  useEffect(() => {
    checkAuth();
  }, []);

  const login = async (credentials) => {
    await AuthService.login(credentials);
    setIsAuthenticated(true);
  };

  const logout = async () => {
    await AuthService.logout();
    setIsAuthenticated(false);
  };

  const checkAuth = () => {
    const authStatus = AuthService.isAuthenticated();
    setIsAuthenticated(authStatus);
    return authStatus;
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout, checkAuth }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);