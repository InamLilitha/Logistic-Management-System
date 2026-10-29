import React, { useState, useEffect } from 'react';
import { Truck, ArrowLeft, Mail, KeyRound } from 'lucide-react';
import axios from 'axios';
import { useAuth } from './AuthContext';
import './login.css';

const API_BASE = 'http://localhost:8080/api';

export default function Login() {
  const { login } = useAuth();

  const [view, setView] = useState('login');

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const [email, setEmail] = useState('');
  const [resetMessage, setResetMessage] = useState('');
  const [resetError, setResetError] = useState('');
  const [resetLoading, setResetLoading] = useState(false);

  const [resetToken, setResetToken] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [completeMessage, setCompleteMessage] = useState('');
  const [completeError, setCompleteError] = useState('');
  const [completeLoading, setCompleteLoading] = useState(false);

  // Detect a password-reset token in the email link.
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');

    if (token) {
      setResetToken(token);
      setView('reset');
    }
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');

    try {
      await login(username, password);
    } catch (loginError) {
      setError(
        loginError.message?.includes('401')
          ? 'Invalid username or password.'
          : 'Could not connect to the login service.'
      );
    }
  }

  async function handleForgotPassword(event) {
    event.preventDefault();

    setResetMessage('');
    setResetError('');
    setResetLoading(true);

    try {
      const response = await axios.post(
        `${API_BASE}/password-reset/request`,
        { email: email.trim() },
        { withCredentials: true }
      );

      setResetMessage(
        response.data?.message ||
          'If an account exists for that email, your reset request has been submitted for review.'
      );
    } catch (requestError) {
      setResetError(
        requestError.response?.data?.message ||
          'We could not submit your request. Please try again.'
      );
    } finally {
      setResetLoading(false);
    }
  }

  async function handleCompleteReset(event) {
    event.preventDefault();

    setCompleteError('');
    setCompleteMessage('');

    if (!resetToken) {
      setCompleteError(
        'Your reset link is missing its token. Please request a new reset.'
      );
      return;
    }

    if (newPassword.length < 12) {
      setCompleteError(
        'Your password must contain at least 12 characters.'
      );
      return;
    }

    if (newPassword !== confirmPassword) {
      setCompleteError('Your passwords do not match.');
      return;
    }

    setCompleteLoading(true);

    try {
      const response = await axios.post(
        `${API_BASE}/password-reset/complete`,
        {
          token: resetToken,
          newPassword: newPassword
        },
        { withCredentials: true }
      );

      setCompleteMessage(
        response.data?.message ||
          'Your password has been reset successfully.'
      );

      setNewPassword('');
      setConfirmPassword('');

      // Remove the token from the address bar after a successful reset.
      window.history.replaceState(
        {},
        document.title,
        window.location.pathname
      );
    } catch (requestError) {
      setCompleteError(
        requestError.response?.data?.message ||
          requestError.response?.data?.error ||
          'Unable to reset your password. The link may have expired or may not have been approved.'
      );
    } finally {
      setCompleteLoading(false);
    }
  }

  function showLogin() {
    setView('login');
    setError('');
    setResetError('');
    setResetMessage('');
    setCompleteError('');
    setCompleteMessage('');
    setResetToken('');

    window.history.replaceState(
      {},
      document.title,
      window.location.pathname
    );
  }

  return (
    <main className="login-page">
      <section className="login-card">

        <div className="login-brand">
          <span className="login-brand-icon">
            <Truck size={24} />
          </span>
          <span>LMS Platform</span>
        </div>

        {view === 'login' ? (
          <>
            <h1>Welcome back</h1>

            <p className="login-subtitle">
              Sign in to manage your logistics operations.
            </p>

            <form onSubmit={handleSubmit} className="login-form">

              <label htmlFor="username">
                Username
                <input
                  id="username"
                  value={username}
                  onChange={event => setUsername(event.target.value)}
                  autoComplete="username"
                  required
                />
              </label>

              <label htmlFor="password">
                Password
                <input
                  id="password"
                  type="password"
                  value={password}
                  onChange={event => setPassword(event.target.value)}
                  autoComplete="current-password"
                  required
                />
              </label>

              <div className="login-forgot-row">
                <button
                  type="button"
                  className="login-link"
                  onClick={() => {
                    setView('forgot');
                    setError('');
                  }}
                >
                  Forgot password?
                </button>
              </div>

              {error && (
                <p className="login-error" role="alert">
                  {error}
                </p>
              )}

              <button type="submit">
                Sign in
              </button>

            </form>
          </>
        ) : view === 'forgot' ? (
          <>
            <div className="forgot-icon">
              <Mail size={25} />
            </div>

            <h1>Forgot password?</h1>

            <p className="login-subtitle">
              Enter the email address associated with your staff account.
              Your request will be submitted for administrator review.
            </p>

            <form
              onSubmit={handleForgotPassword}
              className="login-form"
            >
              <label htmlFor="reset-email">
                Staff email address
                <input
                  id="reset-email"
                  type="email"
                  value={email}
                  onChange={event => setEmail(event.target.value)}
                  placeholder="you@example.com"
                  autoComplete="email"
                  required
                />
              </label>

              {resetMessage && (
                <p className="login-success" role="status">
                  {resetMessage}
                </p>
              )}

              {resetError && (
                <p className="login-error" role="alert">
                  {resetError}
                </p>
              )}

              <button type="submit" disabled={resetLoading}>
                {resetLoading
                  ? 'Submitting request...'
                  : 'Submit reset request'}
              </button>
            </form>

            <button
              type="button"
              className="back-to-login"
              onClick={showLogin}
            >
              <ArrowLeft size={16} />
              Back to login
            </button>
          </>
        ) : (
          <>
            <div className="forgot-icon">
              <KeyRound size={25} />
            </div>

            <h1>Reset your password</h1>

            <p className="login-subtitle">
              Choose a new password for your LMS account.
            </p>

            {completeError && (
              <p className="login-error" role="alert">
                {completeError}
              </p>
            )}

            {completeMessage && (
              <p className="login-success" role="status">
                {completeMessage}
              </p>
            )}

            {!completeMessage && (
              <form
                onSubmit={handleCompleteReset}
                className="login-form"
              >
                <label htmlFor="new-password">
                  New password
                  <input
                    id="new-password"
                    type="password"
                    value={newPassword}
                    onChange={event => setNewPassword(event.target.value)}
                    autoComplete="new-password"
                    minLength={12}
                    required
                  />
                </label>

                <label htmlFor="confirm-password">
                  Confirm new password
                  <input
                    id="confirm-password"
                    type="password"
                    value={confirmPassword}
                    onChange={event => setConfirmPassword(event.target.value)}
                    autoComplete="new-password"
                    minLength={12}
                    required
                  />
                </label>

                <small>
                  Password must contain at least 12 characters.
                </small>

                <button type="submit" disabled={completeLoading}>
                  {completeLoading
                    ? 'Resetting password...'
                    : 'Reset password'}
                </button>
              </form>
            )}

            {completeMessage && (
              <button
                type="button"
                className="back-to-login"
                onClick={showLogin}
              >
                <ArrowLeft size={16} />
                Back to login
              </button>
            )}
          </>
        )}

      </section>
    </main>
  );
}

