const API_BASE = 'http://localhost:8080/api';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(
        message || `Request failed with status ${response.status}`
    );
  }

  return response.status === 204 ? null : response.json();
}


export function login(username, password) {
  return request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
}

export function createStaff(profile) {
  return request('/staff', {
    method: 'POST',
    body: JSON.stringify(profile),
  });
}

export function getStaffProfiles() {
  return request('/staff');
}

export function updateStaff(id, profile) {
  return request(`/staff/${id}`, {
    method: 'PUT',
    body: JSON.stringify(profile),
  });
}


export function setStaffAccess(id, active) {
  return request(`/staff/${id}/access`, {
    method: 'PATCH',
    body: JSON.stringify({ active }),
  });
}

export function deleteStaff(id) {
  return request(`/staff/${id}`, {
    method: 'DELETE',
  });
}
export function getPendingPasswordResets() {
  return request('/password-reset/admin/pending');
}

export function approvePasswordReset(id) {
  return request(`/password-reset/admin/${id}/approve`, {
method: 'PATCH',
});
}
export function getPasswordResetHistory() {
  return request('/password-reset/admin/history');
}
export function rejectPasswordReset(id) {
  return request(`/password-reset/admin/${id}/reject`, {
    method: 'PATCH',
  });
}
