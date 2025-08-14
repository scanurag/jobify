import React, { createContext, useState, useEffect } from 'react';
import axios from 'axios';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token') || '');

  const BASE_URL = process.env.REACT_APP_API_URL;
  console.log("BASE_URL:", BASE_URL);

  // ✅ Safe token decode function
  const decodeToken = (jwtToken) => {
    try {
      if (!jwtToken || typeof jwtToken !== "string") return null;
      const parts = jwtToken.split('.');
      if (parts.length !== 3) return null;
      const base64Url = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const decoded = JSON.parse(atob(base64Url));
      return decoded;
    } catch (err) {
      console.error("Invalid token format:", err);
      return null;
    }
  };

  useEffect(() => {
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      const decoded = decodeToken(token);
      if (decoded) {
        setUser({ email: decoded.sub, role: decoded.role });
      } else {
        // ❌ Invalid token → logout
        localStorage.removeItem('token');
        setToken('');
        setUser(null);
      }
    }
  }, [token]);

  const login = async (email, password, type) => {
    const response = await axios.post(`${BASE_URL}/login/${type}`, { email, password });
    const newToken = response.data.token;
    setToken(newToken);
    localStorage.setItem('token', newToken);
    axios.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
    const decoded = decodeToken(newToken);
    if (decoded) {
      setUser({ email: decoded.sub, role: decoded.role });
    }
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
