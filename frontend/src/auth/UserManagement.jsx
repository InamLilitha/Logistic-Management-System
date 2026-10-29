
import React, { useEffect, useState } from 'react';
import {
  PauseCircle,
  PlayCircle,
  Trash2,
  UserPlus,
  Pencil,
  X,
} from 'lucide-react';

import { useAuth } from './AuthContext';

import {
  deleteStaff,
  getStaffProfiles,
  setStaffAccess,
  updateStaff,
} from '../services/AuthService';

const EMPTY_FORM = {
  name: '',
  username: '',
  email: '',
  password: '',
};

export default function UserManagement() {
  const { createStaff } = useAuth();

  const [staffUsers, setStaffUsers] = useState([]);
  const [form, setForm] = useState({ ...EMPTY_FORM });
  const [editingUser, setEditingUser] = useState(null);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  async function loadStaff() {
    const profiles = await getStaffProfiles();
    setStaffUsers(profiles);
  }

  useEffect(() => {
    loadStaff().catch(() => {
      setError('Could not load staff profiles from the database.');
    });
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;

    setForm(previous => ({
      ...previous,
      [name]: value,
    }));
  }

  function startEditing(user) {
    setEditingUser(user);

    setForm({
      name: user.name || '',
      username: user.username || '',
      email: user.email || '',
      password: '',
    });

    setMessage('');
    setError('');
  }

  function cancelEditing() {
    setEditingUser(null);
    setForm({ ...EMPTY_FORM });
    setError('');
    setMessage('');
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setMessage('');
    setError('');
    setSaving(true);

    try {
      const profile = {
        name: form.name.trim(),
        username: form.username.trim(),
        email: form.email.trim(),
      };

      if (editingUser) {
        await updateStaff(editingUser.id, profile);

        setMessage(
          `Profile for ${profile.name} was updated successfully.`
        );
      } else {
        await createStaff({
          ...profile,
          password: form.password,
        });

        setMessage(
          `Staff profile for ${profile.name} was created successfully.`
        );
      }

      await loadStaff();

      setEditingUser(null);
      setForm({ ...EMPTY_FORM });
    } catch (submitError) {
      setError(
        submitError.message ||
          'Could not save the staff profile.'
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleAccessChange(user) {
    setError('');
    setMessage('');

    try {
      await setStaffAccess(user.id, !user.active);
      await loadStaff();

      setMessage(
        `${user.name}'s access is now ${
user.active ? 'paused' : 'active'
}.`
      );
    } catch (accessError) {
      setError(
        accessError.message ||
          'Could not change staff access.'
      );
    }
  }

  async function handleDelete(user) {
    if (
      !window.confirm(
        `Permanently delete the profile for ${user.name}?`
      )
    ) {
      return;
    }

    setError('');
    setMessage('');

    try {
      await deleteStaff(user.id);
      await loadStaff();

      if (editingUser?.id === user.id) {
        cancelEditing();
      }

      setMessage(
        `Staff profile for ${user.name} was deleted.`
      );
    } catch (deleteError) {
      setError(
        deleteError.message ||
          'Could not delete the staff profile.'
      );
    }
  }

  return (
    <div className="p-8">
      <div className="grid max-w-6xl grid-cols-1 gap-8 lg:grid-cols-2">

        {/* CREATE / EDIT FORM */}
        <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <h1 className="mb-1 flex items-center gap-2 text-xl font-bold text-lms-textMain">
            {editingUser ? (
              <Pencil className="h-5 w-5 text-lms-action" />
            ) : (
              <UserPlus className="h-5 w-5 text-lms-action" />
            )}

            {editingUser
              ? 'Edit Staff Profile'
              : 'Create Staff Profile'}
          </h1>

          <p className="mb-6 text-sm text-lms-textMuted">
            {editingUser
              ? 'Update the selected staff member’s details.'
              : 'Create an account for a new staff member.'}
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label
                htmlFor="staff-name"
                className="mb-1 block text-sm font-medium text-lms-textMain"
              >
                Full name
              </label>

              <input
                id="staff-name"
                name="name"
                type="text"
                className="w-full rounded border p-2.5 text-sm"
                value={form.name}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label
                htmlFor="staff-username"
                className="mb-1 block text-sm font-medium text-lms-textMain"
              >
                Username
              </label>

              <input
                id="staff-username"
                name="username"
                type="text"
                className="w-full rounded border p-2.5 text-sm"
                value={form.username}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label
                htmlFor="staff-email"
                className="mb-1 block text-sm font-medium text-lms-textMain"
              >
                Email address
              </label>

              <input
                id="staff-email"
                name="email"
                type="email"
                className="w-full rounded border p-2.5 text-sm"
                value={form.email}
                onChange={handleChange}
                required
              />
            </div>

            {!editingUser && (
              <div>
                <label
                  htmlFor="staff-password"
                  className="mb-1 block text-sm font-medium text-lms-textMain"
                >
                  Password
                </label>

                <input
                  id="staff-password"
                  name="password"
                  type="password"
                  autoComplete="new-password"
                  minLength={12}
                  className="w-full rounded border p-2.5 text-sm"
                  value={form.password}
                  onChange={handleChange}
                  required
                />

                <p className="mt-1 text-xs text-lms-textMuted">
                  At least 12 characters.
                </p>
              </div>
            )}

            {error && (
              <p className="text-sm text-lms-danger" role="alert">
                {error}
              </p>
            )}

            {message && (
              <p className="text-sm text-lms-success" role="status">
                {message}
              </p>
            )}

            <div className="flex gap-3">
              <button
                type="submit"
                disabled={saving}
                className="flex-1 rounded bg-lms-action py-2.5 text-sm font-medium text-white hover:opacity-90 disabled:opacity-50"
              >
                {saving
                  ? 'Saving...'
                  : editingUser
                    ? 'Save Changes'
                    : 'Create Staff'}
              </button>

              {editingUser && (
                <button
                  type="button"
                  onClick={cancelEditing}
                  className="flex items-center gap-2 rounded border px-4 py-2.5 text-sm font-medium hover:bg-slate-50"
                >
                  <X className="h-4 w-4" />
                  Cancel
                </button>
              )}
            </div>
          </form>
        </section>

        {/* STAFF LIST */}
        <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="mb-4 text-lg font-bold text-lms-textMain">
            Staff Profiles ({staffUsers.length})
          </h2>

          {staffUsers.length === 0 ? (
            <p className="text-sm text-lms-textMuted">
              No staff profiles have been created.
            </p>
          ) : (
            <ul className="divide-y divide-slate-100">
              {staffUsers.map(user => (
                <li
                  key={user.id}
                  className="flex flex-wrap items-center justify-between gap-3 py-4 text-sm"
                >
                  <div className="min-w-0">
                    <p className="font-medium text-lms-textMain">
                      {user.name}
                    </p>

                    <p className="text-lms-textMuted">
                      @{user.username}
                    </p>

                    <p className="break-all text-lms-textMuted">
                      {user.email || 'No email registered'}
                    </p>

                    <span
                      className={`mt-2 inline-block rounded-full px-2.5 py-1 text-xs font-semibold ${
  user.active
      ? 'bg-green-50 text-lms-success'
      : 'bg-slate-100 text-lms-textMuted'
}`}
                    >
                      {user.active ? 'Active' : 'Paused'}
                    </span>
                  </div>

                  <div className="flex items-center gap-1">
                    <button
                      type="button"
                      onClick={() => startEditing(user)}
                      className="rounded p-2 text-lms-action hover:bg-slate-100"
                      title="Edit profile"
                      aria-label={`Edit ${user.name}`}
                    >
                      <Pencil className="h-4 w-4" />
                    </button>

                    <button
                      type="button"
                      onClick={() => handleAccessChange(user)}
                      className="rounded p-2 text-amber-600 hover:bg-amber-50"
                      title={
                        user.active
                          ? 'Pause access'
                          : 'Restore access'
                      }
                      aria-label={`${
  user.active ? 'Pause' : 'Restore'
} ${user.name}`}
                    >
                      {user.active ? (
                        <PauseCircle className="h-4 w-4" />
                      ) : (
                        <PlayCircle className="h-4 w-4" />
                      )}
                    </button>

                    <button
                      type="button"
                      onClick={() => handleDelete(user)}
                      className="rounded p-2 text-lms-danger hover:bg-red-50"
                      title="Delete profile"
                      aria-label={`Delete ${user.name}`}
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>
    </div>
  );
}

