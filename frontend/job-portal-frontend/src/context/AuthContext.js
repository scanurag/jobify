import React, { createContext, useState, useEffect } from 'react';
import axios from 'axios';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token') || '');

 const BASE_URL = process.env.REACT_APP_API_URL || 'https://jobify-9pw8.onrender.com/api';
console.log("BASE_URL:", process.env.REACT_APP_API_URL);

  useEffect(() => {
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      try {
        const decoded = JSON.parse(atob(token.split('.')[1]));
        setUser({ email: decoded.sub, role: decoded.role });
      } catch (e) {
        console.error('Invalid token format', e);
        setUser(null);
      }
    }
  }, [token]);

  const login = async (email, password, type) => {
    const response = await axios.post(`${BASE_URL}/login/${type}`, { email, password });
    const { token } = response.data;
    setToken(token);
    localStorage.setItem('token', token);
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    const decoded = JSON.parse(atob(token.split('.')[1]));
    setUser({ email: decoded.sub, role: decoded.role });
  };

  const signup = async (userData, type) => {
    await axios.post(`${BASE_URL}/signup/${type}`, userData);
  };

  const logout = () => {
    setToken('');
    setUser(null);
    localStorage.removeItem('token');
    delete axios.defaults.headers.common['Authorization'];
  };

  return (
    <AuthContext.Provider value={{ user, token, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

