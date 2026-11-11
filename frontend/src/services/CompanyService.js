import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/companies';

export const getAllCompanies = () => axios.get(`${API_BASE}/getall`, { withCredentials: true });
export const getCompanyById = (id) => axios.get(`${API_BASE}/read/${id}`, { withCredentials: true });
export const createCompany = (company) => axios.post(`${API_BASE}/create`, company, { withCredentials: true });
export const updateCompany = (company) => axios.put(`${API_BASE}/update`, company, { withCredentials: true });
export const deleteCompany = (id) => axios.delete(`${API_BASE}/delete/${id}`, { withCredentials: true });