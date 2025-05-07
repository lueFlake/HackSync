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
      setAuthState(prev => ({ ...prev, isAuthenticated: isAuth, isLoading: false }));
      return isAuth;
    } catch (error) {
      setAuthState(prev => ({ ...prev, isLoading: false, error }));
      return false;
    }
  }, []);

  // On mount, just check authentication status
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
      // Store user data in memory only
      console.log(data)
      let user = {
        userId: data.userId,
        email: data.email,
        name: data.name
      }
      setAuthState(prev => ({
        ...prev,
        isAuthenticated: true,
        user: user, // Assuming login response includes user data
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