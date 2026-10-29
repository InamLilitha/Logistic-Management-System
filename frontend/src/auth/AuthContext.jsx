import React, { createContext, useContext, useMemo, useState } from 'react';
import { createStaff as createStaffRequest, login as loginRequest } from '../services/AuthService';

const SESSION_KEY = 'lms-session';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem(SESSION_KEY) || 'null');
    } catch {
      return null;
    }
  });

  const value = useMemo(() => ({
    currentUser,
    isAdmin: currentUser?.role === 'admin',
    async login(username, password) {
      const sessionUser = await loginRequest(username.trim(), password);
      localStorage.setItem(SESSION_KEY, JSON.stringify(sessionUser));
      setCurrentUser(sessionUser);
      return sessionUser;
    },
    logout() {
      localStorage.removeItem(SESSION_KEY);
      setCurrentUser(null);
    },
async createStaff(profile) {
  return createStaffRequest({
    ...profile,
    name: profile.name.trim(),
    username: profile.username.trim(),
    email: profile.email.trim(),
  });
},


}), [currentUser]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
