import React, { useEffect, useState } from 'react';
import { getInvoices, createInvoice } from '../services/InvoiceService';
import './Invoices.css'; // Keep your existing CSS file

function Invoices() {
    const [invoices, setInvoices] = useState([]);
    const [formData, setFormData] = useState({
        invoiceId: '',
        total: '',
        paymentStatus: 'Pending',
        dateIssued: new Date().toISOString().slice(0, 10),
    });

    useEffect(() => {
        loadInvoices();
    }, []);

    async function loadInvoices() {
        try {
            const data = await getInvoices();
            setInvoices(data);
        } catch (error) {
            console.error("Failed to load invoices from the backend", error);
        }
    }

    function handleInputChange(event) {
        const { name, value } = event.target;
        setFormData(current => ({ ...current, [name]: value }));
    }

    async function handleSubmit(event) {
        event.preventDefault();
        
        try {
            const newInvoice = {
                ...formData,
                total: Number(formData.total),
                dateIssued: `${formData.dateIssued}T00:00:00.000Z`,
            };
            
            await createInvoice(newInvoice);
            
            setFormData({
                invoiceId: '',
                total: '',
                paymentStatus: 'Pending',
                dateIssued: new Date().toISOString().slice(0, 10)
            });
            loadInvoices();

        } catch (error) {
            console.error("Failed to create invoice", error);
        }
    }

    return (
        <div className="invoices-container">
            <header className="page-header">
                <h1>Invoices</h1>
                <p>Iteration 1: Create and Read</p>
            </header>

            {/* Basic Form */}
            <section className="invoice-form-card">
                <h2>Make an Invoice</h2>
                <form className="invoice-form" onSubmit={handleSubmit}>
                    <input 
                        name="invoiceId" 
                        placeholder="INV-001" 
                        value={formData.invoiceId} 
                        onChange={handleInputChange} 
                        required 
                    />
                    <input 
                        name="total" 
                        type="number" 
                        placeholder="Total Amount" 
                        value={formData.total} 
                        onChange={handleInputChange} 
                        required 
                    />
                    <select 
                        name="paymentStatus" 
                        value={formData.paymentStatus} 
                        onChange={handleInputChange}
                    >
                        <option value="Pending">Pending</option>
                        <option value="Paid">Paid</option>
                    </select>
                    <input 
                        name="dateIssued" 
                        type="date" 
                        value={formData.dateIssued} 
                        onChange={handleInputChange} 
                        required 
                    />
                    <button type="submit" className="save-invoice-btn">Save</button>
                </form>
            </section>

            {/* Basic Table */}
            <div className="invoices-list-container">
                <table className="invoices-table">
                    <thead>
                        <tr>
                            <th>Invoice ID</th>
                            <th>Total</th>
                            <th>Status</th>
                            <th>Date Issued</th>
                        </tr>
                    </thead>
                    <tbody>
                        {invoices.length === 0 ? (
                            <tr><td colSpan="4">No invoices loaded yet.</td></tr>
                        ) : (
                            invoices.map(invoice => (
                                <tr key={invoice.invoiceId}>
                                    <td>{invoice.invoiceId}</td>
                                    <td>{Number(invoice.total).toFixed(2)}</td>
                                    <td>{invoice.paymentStatus}</td>
                                    <td>{new Date(invoice.dateIssued).toLocaleDateString()}</td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default Invoices;