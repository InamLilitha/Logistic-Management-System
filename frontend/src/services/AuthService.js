const API_BASE = 'http://localhost:8080/api';

async function request(path, options) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed with status ${response.status}`);
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
