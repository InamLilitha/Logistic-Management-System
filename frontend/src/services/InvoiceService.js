import axios from 'axios';

const API_BASE = 'http://localhost:8080/invoices';


export async function getInvoices() {
    const response = await axios.get(`${API_BASE}/getall`);
    return response.data;
}


export async function createInvoice(invoice) {
    const response = await axios.post(`${API_BASE}/create`, invoice);
    return response.data;
}


export async function updateInvoice(invoice) {
    // Notice this uses POST to match your Java @PostMapping
    const response = await axios.post(`${API_BASE}/update`, invoice);
    return response.data;
}


export async function deleteInvoice(id) {
    await axios.delete(`${API_BASE}/delete/${id}`);
}