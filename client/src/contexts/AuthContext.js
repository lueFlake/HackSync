import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import AuthService from '../services/AuthService';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [authState, setAuthState] = useState({
    isAuthenticated: false,
    isLoading: true,
    error: null
  });

  const checkAuth = useCallback(async () => {
    try {
      const isAuth = await AuthService.isAuthenticated();
      setAuthState(prev => ({ ...prev, isAuthenticated: isAuth, isLoading: false }));
      return isAuth;
    } catch (error) {
      setAuthState(prev => ({ ...prev, isLoading: false, error }));
      return false;
    }
  }, []);

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  const register = async (userData) => {
    try {
      await AuthService.register(userData);
    } catch (error) {
      setAuthState(prev => ({ ...prev, error }));
      throw error;
    }
  };

  const login = async (credentials) => {
    try {
      await AuthService.login(credentials);
      setAuthState(prev => ({ ...prev, isAuthenticated: true, error: null }));
    } catch (error) {
      setAuthState(prev => ({ ...prev, error }));
      throw error;
    }
  };

  const logout = async () => {
    try {
      await AuthService.logout();
      setAuthState(prev => ({ ...prev, isAuthenticated: false, error: null }));
    } catch (error) {
      setAuthState(prev => ({ ...prev, error }));
      throw error;
    }
  };

  return (
    <AuthContext.Provider
      value={{
        ...authState,
        login,
        logout,
        register,
        checkAuth
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);