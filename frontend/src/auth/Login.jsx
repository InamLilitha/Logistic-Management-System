import React, { useState } from 'react';
import { Truck } from 'lucide-react';
import { useAuth } from './AuthContext';
import './login.css';

export default function Login() {
  const { login } = useAuth();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    try {
      await login(username, password);
    } catch (loginError) {
      setError(loginError.message.includes('401') ? 'Invalid username or password.' : 'Could not connect to the login service.');
    }
  }

  return (
    <main className="login-page">
      <section className="login-card">
        <div className="login-brand">
          <span className="login-brand-icon"><Truck size={24} /></span>
          <span>LMS Platform</span>
        </div>
        <h1>Welcome back</h1>
        <p className="login-subtitle">Sign in to manage your logistics operations.</p>
        <form onSubmit={handleSubmit} className="login-form">
          <label htmlFor="username">Username
            <input id="username" value={username} onChange={event => setUsername(event.target.value)} autoComplete="username" required />
          </label>
          <label htmlFor="password">Password
            <input id="password" type="password" value={password} onChange={event => setPassword(event.target.value)} autoComplete="current-password" required />
          </label>
          {error && <p className="login-error" role="alert">{error}</p>}
          <button type="submit">Sign in</button>
        </form>
        <p className="login-hint">Default administrator: <strong>admin</strong> / <strong>admin123</strong></p>
      </section>
    </main>
  );
}
