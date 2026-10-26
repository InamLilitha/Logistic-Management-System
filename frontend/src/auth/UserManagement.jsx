import React, { useEffect, useState } from 'react';
import { PauseCircle, PlayCircle, Trash2, UserPlus } from 'lucide-react';
import { useAuth } from './AuthContext';
import { deleteStaff, getStaffProfiles, setStaffAccess } from '../services/AuthService';

export default function UserManagement() {
  const { createStaff } = useAuth();
  const [staffUsers, setStaffUsers] = useState([]);
  const [form, setForm] = useState({ name: '', username: '', password: '' });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    getStaffProfiles().then(setStaffUsers).catch(() => setError('Could not load staff profiles from the database.'));
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();
    setMessage('');
    setError('');
    try {
      await createStaff(form);
    } catch (createError) {
      setError(createError.message || 'Could not create the staff profile.');
      return;
    }
    setStaffUsers(await getStaffProfiles());
    setMessage(`Staff profile for ${form.name} was created.`);
    setForm({ name: '', username: '', password: '' });
  }

  async function handleAccessChange(user) {
    setError('');
    setMessage('');
    try {
      await setStaffAccess(user.id, !user.active);
      setStaffUsers(await getStaffProfiles());
      setMessage(`${user.name}'s access is now ${user.active ? 'paused' : 'active'}.`);
    } catch (accessError) {
      setError(accessError.message || 'Could not change staff access.');
    }
  }

  async function handleDelete(user) {
    if (!window.confirm(`Permanently delete the profile for ${user.name}?`)) return;
    setError('');
    setMessage('');
    try {
      await deleteStaff(user.id);
      setStaffUsers(await getStaffProfiles());
      setMessage(`Staff profile for ${user.name} was deleted.`);
    } catch (deleteError) {
      setError(deleteError.message || 'Could not delete the staff profile.');
    }
  }

  return (
    <div className="p-8">
      <div className="grid max-w-5xl grid-cols-1 gap-8 lg:grid-cols-2">
        <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <h1 className="mb-1 flex items-center gap-2 text-xl font-bold text-lms-textMain">
            <UserPlus className="h-5 w-5 text-lms-action" /> Create Staff Profile
          </h1>
          <p className="mb-6 text-sm text-lms-textMuted">Staff can view dashboards and tables, but cannot create, update, or delete records.</p>
          <form onSubmit={handleSubmit} className="space-y-4">
            <input className="w-full rounded border p-2.5 text-sm" placeholder="Full name" value={form.name} onChange={event => setForm({ ...form, name: event.target.value })} required />
            <input className="w-full rounded border p-2.5 text-sm" placeholder="Username" value={form.username} onChange={event => setForm({ ...form, username: event.target.value })} required />
            <input className="w-full rounded border p-2.5 text-sm" type="password" placeholder="Temporary password" minLength="6" value={form.password} onChange={event => setForm({ ...form, password: event.target.value })} required />
            {error && <p className="text-sm text-lms-danger" role="alert">{error}</p>}
            {message && <p className="text-sm text-lms-success" role="status">{message}</p>}
            <button type="submit" className="w-full rounded bg-lms-action py-2.5 text-sm font-medium text-white">Create Staff</button>
          </form>
        </section>
        <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="mb-4 text-lg font-bold text-lms-textMain">Staff Profiles</h2>
          {staffUsers.length === 0 ? <p className="text-sm text-lms-textMuted">No staff profiles have been created.</p> : (
            <ul className="divide-y divide-slate-100">
              {staffUsers.map(user => (
                <li key={user.id} className="flex flex-wrap items-center justify-between gap-3 py-3 text-sm">
                  <div>
                    <p className="font-medium">{user.name}</p>
                    <p className="text-lms-textMuted">@{user.username}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${user.active ? 'bg-green-50 text-lms-success' : 'bg-slate-100 text-lms-textMuted'}`}>
                      {user.active ? 'Active' : 'Paused'}
                    </span>
                    <button type="button" onClick={() => handleAccessChange(user)} className="rounded p-1.5 text-amber-600 hover:bg-amber-50" title={user.active ? 'Pause access' : 'Restore access'} aria-label={`${user.active ? 'Pause' : 'Restore'} ${user.name}`}>
                      {user.active ? <PauseCircle className="h-4 w-4" /> : <PlayCircle className="h-4 w-4" />}
                    </button>
                    <button type="button" onClick={() => handleDelete(user)} className="rounded p-1.5 text-lms-danger hover:bg-red-50" title="Delete profile" aria-label={`Delete ${user.name}`}>
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
