import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/shipping-contract';

export const getAllContracts = () => axios.get(API_BASE);
export const createContract = (contract) => axios.post(API_BASE, contract);
export const updateContract = (contract) => axios.put(API_BASE, contract);
export const deleteContract = (id) => axios.delete(`${API_BASE}/${id}`);
