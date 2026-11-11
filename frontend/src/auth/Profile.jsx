import React, { useEffect, useState } from 'react';
import {
  LogOut,
  ShieldCheck,
  UserCircle,
  Check,
  X,
  RefreshCw,
  History,
  Filter,
} from 'lucide-react';

import { useAuth } from './AuthContext';

import {
  getPendingPasswordResets,
  getPasswordResetHistory,
  approvePasswordReset,
  rejectPasswordReset,
} from '../services/AuthService.js';



export default function Profile() {
  const { currentUser, logout } = useAuth();

  const isAdmin = currentUser?.role?.toLowerCase() === 'admin';
  const roleLabel = isAdmin ? 'Administrator' : 'Staff member';

  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [processingId, setProcessingId] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [history, setHistory] = useState([]);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [historyFilter, setHistoryFilter] = useState('ALL');
  const [historyError, setHistoryError] = useState('');

  const loadRequests = async () => {
    if (!isAdmin) return;

setLoading(true);
setError('');

try {
  const data = await getPendingPasswordResets();
  setRequests(Array.isArray(data) ? data : []);
} catch (err) {
  setError(err.message || 'Could not load password-reset requests.');
} finally {
  setLoading(false);
}


  };

  useEffect(() => {
    if (isAdmin) {
      loadRequests();
      loadHistory();
    }
  }, [isAdmin]);
  const loadHistory = async () => {
    if (!isAdmin) return;

    setHistoryLoading(true);
    setHistoryError('');

    try {
      const data = await getPasswordResetHistory();
      setHistory(Array.isArray(data) ? data : []);
    } catch (err) {
      setHistoryError(err.message || 'Could not load password-reset history.');
    } finally {
      setHistoryLoading(false);
    }
  };

  const handleDecision = async (requestId, action) => {
    const actionLabel = action === 'approve' ? 'approve' : 'reject';


if (!window.confirm(`Are you sure you want to ${actionLabel} this password-reset request?`)) {
  return;
}

setProcessingId(requestId);
setError('');
setMessage('');

try {
  if (action === 'approve') {
    await approvePasswordReset(requestId);
  } else {
    await rejectPasswordReset(requestId);
  }

  setMessage(`Password-reset request ${action === 'approve' ? 'approved' : 'rejected'} successfully.`);
  await Promise.all([loadRequests(), loadHistory()]);
} catch (err) {
  setError(err.message || `Could not ${actionLabel} the request.`);
} finally {
  setProcessingId(null);
}


  };

  return ( <div className="space-y-8 p-8">


    {/* PROFILE CARD */}
    <section className="max-w-2xl rounded-lg border border-slate-200 bg-white p-8 shadow-sm">
      <div className="flex items-center gap-4 border-b border-slate-100 pb-6">
      <span className="grid h-16 w-16 place-items-center rounded-full bg-lms-ice text-lms-navy">
        <UserCircle className="h-10 w-10" />
      </span>

        <div>
          <h1 className="text-2xl font-bold text-lms-textMain">
            {currentUser.name}
          </h1>
          <p className="text-sm text-lms-textMuted">
            @{currentUser.username}
          </p>
        </div>
      </div>

      <dl className="grid gap-4 py-6 sm:grid-cols-2">
        <div>
          <dt className="text-xs font-semibold uppercase tracking-wide text-lms-textMuted">
            Profile type
          </dt>
          <dd className="mt-1 flex items-center gap-2 font-medium text-lms-textMain">
            <ShieldCheck className="h-4 w-4 text-lms-action" />
            {roleLabel}
          </dd>
        </div>

        <div>
          <dt className="text-xs font-semibold uppercase tracking-wide text-lms-textMuted">
            Username
          </dt>
          <dd className="mt-1 font-medium text-lms-textMain">
            {currentUser.username}
          </dd>
        </div>
      </dl>

      <div className="rounded-md bg-slate-50 p-4 text-sm text-lms-textMuted">
        {isAdmin
            ? 'You have administrative access to manage logistics records, staff profiles, and password-reset requests.'
            : 'You have read-only access to the dashboard analytics and logistics tables.'}
      </div>

      <button
          type="button"
          onClick={logout}
          className="mt-6 flex items-center gap-2 rounded bg-lms-navy px-4 py-2.5 text-sm font-medium text-white transition hover:opacity-90"
      >
        <LogOut className="h-4 w-4" />
        Log out
      </button>
    </section>

    {/* ADMIN PASSWORD RESET REQUESTS */}
    {isAdmin && (
        <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
            <div>
              <h2 className="text-xl font-bold text-lms-textMain">
                Password Reset Requests
              </h2>
              <p className="mt-1 text-sm text-lms-textMuted">
                Review staff requests and approve or reject them.
              </p>
            </div>

            <button
                type="button"
                onClick={loadRequests}
                disabled={loading}
                className="flex items-center gap-2 rounded-md border border-slate-200 px-4 py-2 text-sm font-medium text-lms-navy transition hover:bg-slate-50 disabled:opacity-50"
            >
              <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
              Refresh
            </button>
          </div>

          {message && (
              <div role="status" className="mb-4 rounded-md bg-green-50 p-3 text-sm text-green-700">
                {message}
              </div>
          )}

          {error && (
              <div role="alert" className="mb-4 rounded-md bg-red-50 p-3 text-sm text-red-700">
                {error}
              </div>
          )}

          {loading ? (
              <p className="py-8 text-center text-sm text-lms-textMuted">
                Loading password-reset requests...
              </p>
          ) : requests.length === 0 ? (
              <div className="rounded-lg border border-dashed border-slate-200 py-10 text-center">
                <ShieldCheck className="mx-auto mb-3 h-10 w-10 text-slate-300" />
                <h3 className="font-semibold text-lms-textMain">
                  All caught up!
                </h3>
                <p className="mt-1 text-sm text-lms-textMuted">
                  There are no pending password-reset requests.
                </p>
              </div>
          ) : (
              <>
                <div className="mb-4 flex items-center gap-2">
            <span className="rounded-full bg-amber-100 px-3 py-1 text-sm font-semibold text-amber-800">
              {requests.length} pending {requests.length === 1 ? 'request' : 'requests'}
            </span>
                </div>

                <div className="overflow-x-auto rounded-lg border border-slate-200">
                  <table className="w-full text-left text-sm">
                    <thead className="bg-slate-50 text-xs uppercase tracking-wide text-lms-textMuted">
                    <tr>
                      <th className="px-4 py-3">Staff member</th>
                      <th className="px-4 py-3">Email</th>
                      <th className="px-4 py-3">Requested</th>
                      <th className="px-4 py-3">Actions</th>
                    </tr>
                    </thead>

                    <tbody className="divide-y divide-slate-100">
                    {requests.map((request) => (
                        <tr key={request.id} className="transition hover:bg-slate-50">
                          <td className="px-4 py-4">
                            <p className="font-semibold text-lms-textMain">
                              {request.name}
                            </p>
                            <p className="mt-1 text-xs text-lms-textMuted">
                              @{request.username}
                            </p>
                          </td>

                          <td className="px-4 py-4 text-lms-textMuted">
                            {request.email}
                          </td>

                          <td className="whitespace-nowrap px-4 py-4 text-lms-textMuted">
                            {request.createdAt
                                ? new Date(request.createdAt).toLocaleString()
                                : '—'}
                          </td>

                          <td className="px-4 py-4">
                            <div className="flex flex-wrap gap-2">
                              <button
                                  type="button"
                                  onClick={() => handleDecision(request.id, 'approve')}
                                  disabled={processingId !== null}
                                  className="flex items-center gap-1 rounded-md bg-green-600 px-3 py-2 text-xs font-semibold text-white transition hover:bg-green-700 disabled:opacity-50"
                              >
                                <Check className="h-3.5 w-3.5" />
                                Approve
                              </button>

                              <button
                                  type="button"
                                  onClick={() => handleDecision(request.id, 'reject')}
                                  disabled={processingId !== null}
                                  className="flex items-center gap-1 rounded-md bg-red-50 px-3 py-2 text-xs font-semibold text-red-700 transition hover:bg-red-100 disabled:opacity-50"
                              >
                                <X className="h-3.5 w-3.5" />
                                Reject
                              </button>
                            </div>
                          </td>
                        </tr>
                    ))}
                    </tbody>
                  </table>
                </div>
              </>
          )}
        </section>
    )}

        {/* ADMIN PASSWORD RESET HISTORY */}
        {isAdmin && (
            <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
              <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
                <div>
                  <div className="flex items-center gap-2">
                    <History className="h-5 w-5 text-lms-navy" />
                    <h2 className="text-xl font-bold text-lms-textMain">
                      Password Reset History
                    </h2>
                  </div>

                  <p className="mt-1 text-sm text-lms-textMuted">
                    View previously approved, rejected, and completed requests.
                  </p>
                </div>

                <button
                    type="button"
                    onClick={loadHistory}
                    disabled={historyLoading}
                    className="flex items-center gap-2 rounded-md border border-slate-200 px-4 py-2 text-sm font-medium text-lms-navy transition hover:bg-slate-50 disabled:opacity-50"
                >
                  <RefreshCw className={`h-4 w-4 ${historyLoading ? 'animate-spin' : ''}`} />
                  Refresh history
                </button>
              </div>

              {historyError && (
                  <div role="alert" className="mb-4 rounded-md bg-red-50 p-3 text-sm text-red-700">
                    {historyError}
                  </div>
              )}

              <div className="mb-5 flex flex-wrap items-center gap-3">
                <label
                    htmlFor="historyFilter"
                    className="flex items-center gap-2 text-sm font-medium text-lms-textMain"
                >
                  <Filter className="h-4 w-4" />
                  Filter by status
                </label>

                <select
                    id="historyFilter"
                    value={historyFilter}
                    onChange={(event) => setHistoryFilter(event.target.value)}
                    className="rounded-md border border-slate-200 bg-white px-3 py-2 text-sm text-lms-textMain outline-none focus:border-lms-navy"
                >
                  <option value="ALL">All requests</option>
                  <option value="PENDING">Pending</option>
                  <option value="APPROVED">Approved</option>
                  <option value="REJECTED">Rejected</option>
                  <option value="COMPLETED">Completed</option>
                  <option value="EXPIRED">Expired</option>
                </select>

                <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-600">
        {history.filter(
            (item) => historyFilter === 'ALL' || item.status === historyFilter
        ).length} requests
      </span>
              </div>

              {historyLoading ? (
                  <p className="py-8 text-center text-sm text-lms-textMuted">
                    Loading request history...
                  </p>
              ) : history.filter(
                  (item) => historyFilter === 'ALL' || item.status === historyFilter
              ).length === 0 ? (
                  <div className="rounded-lg border border-dashed border-slate-200 py-10 text-center">
                    <History className="mx-auto mb-3 h-9 w-9 text-slate-300" />
                    <h3 className="font-semibold text-lms-textMain">
                      No requests found
                    </h3>
                    <p className="mt-1 text-sm text-lms-textMuted">
                      No password-reset requests match this filter.
                    </p>
                  </div>
              ) : (
                  <div className="overflow-x-auto rounded-lg border border-slate-200">
                    <table className="w-full text-left text-sm">
                      <thead className="bg-slate-50 text-xs uppercase tracking-wide text-lms-textMuted">
                      <tr>
                        <th className="px-4 py-3">Staff member</th>
                        <th className="px-4 py-3">Requested</th>
                        <th className="px-4 py-3">Decision date</th>
                        <th className="px-4 py-3">Completed date</th>
                        <th className="px-4 py-3">Status</th>
                      </tr>
                      </thead>

                      <tbody className="divide-y divide-slate-100">
                      {history
                          .filter(
                              (item) => historyFilter === 'ALL' || item.status === historyFilter
                          )
                          .map((item) => {
                            const badgeStyles = {
                              PENDING: 'bg-amber-100 text-amber-800',
                              APPROVED: 'bg-blue-100 text-blue-800',
                              REJECTED: 'bg-red-100 text-red-700',
                              COMPLETED: 'bg-green-100 text-green-800',
                              EXPIRED: 'bg-slate-200 text-slate-700',
                            };

                            return (
                                <tr key={item.id} className="transition hover:bg-slate-50">
                                  <td className="px-4 py-4">
                                    <p className="font-semibold text-lms-textMain">
                                      {item.name}
                                    </p>
                                    <p className="mt-1 text-xs text-lms-textMuted">
                                      @{item.username}
                                    </p>
                                    <p className="mt-1 text-xs text-lms-textMuted">
                                      {item.email}
                                    </p>
                                  </td>

                                  <td className="whitespace-nowrap px-4 py-4 text-lms-textMuted">
                                    {item.createdAt
                                        ? new Date(item.createdAt).toLocaleString()
                                        : '—'}
                                  </td>

                                  <td className="whitespace-nowrap px-4 py-4 text-lms-textMuted">
                                    {item.reviewedAt
                                        ? new Date(item.reviewedAt).toLocaleString()
                                        : '—'}
                                  </td>

                                  <td className="whitespace-nowrap px-4 py-4 text-lms-textMuted">
                                    {item.completedAt
                                        ? new Date(item.completedAt).toLocaleString()
                                        : '—'}
                                  </td>

                                  <td className="px-4 py-4">
                      <span
                          className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold ${
                              badgeStyles[item.status] || 'bg-slate-100 text-slate-700'
                          }`}
                      >
                        {item.status}
                      </span>
                                  </td>
                                </tr>
                            );
                          })}
                      </tbody>
                    </table>
                  </div>
              )}
            </section>
        )}



      </div>


);
}
