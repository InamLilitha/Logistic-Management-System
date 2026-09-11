import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/companies';

export const getAllCompanies = () => axios.get(`${API_BASE}/getall`);
export const getCompanyById = (id) => axios.get(`${API_BASE}/read/${id}`);
export const createCompany = (company) => axios.post(`${API_BASE}/create`, company);
export const updateCompany = (company) => axios.put(`${API_BASE}/update`, company);
export const deleteCompany = (id) => axios.delete(`${API_BASE}/delete/${id}`);