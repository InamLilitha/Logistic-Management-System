import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/inventory';

export const getAllInventory = () => axios.get(`${API_BASE}/getall`, { withCredentials: true });
export const getInventoryById = id => axios.get(`${API_BASE}/read/${id}`, { withCredentials: true });
export const createInventory = item => axios.post(`${API_BASE}/create`, null, { withCredentials: true }, {
  params: {
    itemName: item.itemName,
    sku: item.sku,
    quantity: item.quantityAvailable,
    unitWeight: item.unitWeight,
    companyId: item.companyId,
  },
});
export const updateInventory = item => axios.put(`${API_BASE}/update`, item, { withCredentials: true });
export const deleteInventory = id => axios.delete(`${API_BASE}/delete/${id}`, { withCredentials: true });
export const updateInventoryQuantity = (id, quantity) =>
  axios.patch(`${API_BASE}/update-quantity/${id}`, null,{ withCredentials: true }, { params: { quantity } });
