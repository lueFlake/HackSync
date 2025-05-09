import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import AuthService from '../services/AuthService';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [authState, setAuthState] = useState({
    isAuthenticated: false,
    isLoading: true,
    error: null,
    user: null
  });

  const checkAuth = useCallback(async () => {
    try {
      const isAuth = await AuthService.isAuthenticated();
      if (isAuth) {
        // If authenticated, fetch user data
        const userData = await AuthService.getCurrentUser();
        setAuthState(prev => ({
          ...prev,
          isAuthenticated: true,
          user: userData,
          isLoading: false
        }));
      } else {
        setAuthState(prev => ({
          ...prev,
          isAuthenticated: false,
          user: null,
          isLoading: false
        }));
      }
      return isAuth;
    } catch (error) {
      setAuthState(prev => ({
        ...prev,
        isLoading: false,
        error,
        isAuthenticated: false,
        user: null
      }));
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
      const data = await AuthService.login(credentials);
      let user = {
        userId: data.userId,
        email: data.email,
        name: data.name
      }
      setAuthState(prev => ({
        ...prev,
        isAuthenticated: true,
        user: user,
        error: null
      }));
    } catch (error) {
      setAuthState(prev => ({ ...prev, error }));
      throw error;
    }
  };

  const logout = async () => {
    try {
      await AuthService.logout();
    } finally {
      setAuthState({
        isAuthenticated: false,
        isLoading: false,
        error: null,
        user: null
      });
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