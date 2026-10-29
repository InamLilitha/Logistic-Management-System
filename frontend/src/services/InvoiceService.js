import axios from 'axios';

const API_BASE = 'http://localhost:8080/invoices';

const invoiceApi = axios.create({
    baseURL: API_BASE,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});

export async function getInvoices() {
    const response = await invoiceApi.get('/getall');
    return response.data;
}

export async function createInvoice(invoice) {
    const response = await invoiceApi.post('/create', invoice);
    return response.data;
}

export async function updateInvoice(invoice) {
    const response = await invoiceApi.post('/update', invoice);
    return response.data;
}

export async function deleteInvoice(id) {
    await invoiceApi.delete(`/delete/${id}`);
}
