import React from 'react';
import { LogOut, ShieldCheck, UserCircle } from 'lucide-react';
import { useAuth } from './AuthContext';

export default function Profile() {
  const { currentUser, logout } = useAuth();
  const roleLabel = currentUser.role === 'admin' ? 'Administrator' : 'Staff member';

  return (
    <div className="p-8">
      <section className="max-w-2xl rounded-lg border border-slate-200 bg-white p-8 shadow-sm">
        <div className="flex items-center gap-4 border-b border-slate-100 pb-6">
          <span className="grid h-16 w-16 place-items-center rounded-full bg-lms-ice text-lms-navy">
            <UserCircle className="h-10 w-10" />
          </span>
          <div>
            <h1 className="text-2xl font-bold text-lms-textMain">{currentUser.name}</h1>
            <p className="text-sm text-lms-textMuted">@{currentUser.username}</p>
          </div>
        </div>
        <dl className="grid gap-4 py-6 sm:grid-cols-2">
          <div>
            <dt className="text-xs font-semibold uppercase tracking-wide text-lms-textMuted">Profile type</dt>
            <dd className="mt-1 flex items-center gap-2 font-medium text-lms-textMain">
              <ShieldCheck className="h-4 w-4 text-lms-action" /> {roleLabel}
            </dd>
          </div>
          <div>
            <dt className="text-xs font-semibold uppercase tracking-wide text-lms-textMuted">Username</dt>
            <dd className="mt-1 font-medium text-lms-textMain">{currentUser.username}</dd>
          </div>
        </dl>
        <div className="rounded-md bg-slate-50 p-4 text-sm text-lms-textMuted">
          {currentUser.role === 'admin'
            ? 'You have full access to manage logistics records and create staff profiles.'
            : 'You have read-only access to the dashboard analytics and logistics tables.'}
        </div>
        <button type="button" onClick={logout} className="mt-6 flex items-center gap-2 rounded bg-lms-navy px-4 py-2.5 text-sm font-medium text-white">
          <LogOut className="h-4 w-4" /> Log out
        </button>
      </section>
    </div>
  );
}
