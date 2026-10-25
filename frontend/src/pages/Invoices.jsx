import React, { useEffect, useState } from 'react';
import { Pencil, RefreshCw, Trash2, X } from 'lucide-react';
import { getInvoices, createInvoice, updateInvoice, deleteInvoice } from '../services/InvoiceService';
import './Invoices.css';

function Invoices() {
    const [invoices, setInvoices] = useState([]);
    const [formData, setFormData] = useState({
        customerName: '',
        description: '',
        total: '',
        paymentStatus: 'Pending',
        dateIssued: new Date().toISOString().slice(0, 10),
        dueDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
    });
    const [editing, setEditing] = useState(false);
    const [editingInvoiceId, setEditingInvoiceId] = useState(null);
    const [search, setSearch] = useState('');
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        loadInvoices();
    }, []);

    async function loadInvoices() {
        setLoading(true);
        setError('');
        try {
            const data = await getInvoices();
            setInvoices(Array.isArray(data) ? data : []);
        } catch (error) {
            setError('Could not load invoices. Check the server connection and try again.');
        } finally {
            setLoading(false);
        }
    }

    function handleInputChange(event) {
        const { name, value } = event.target;
        setFormData(current => ({ ...current, [name]: value }));
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setSaving(true);
        setError('');
        setMessage('');
        try {
            const newInvoice = {
                ...formData,
                ...(editing ? { invoiceId: editingInvoiceId } : {}),
                total: Number(formData.total),
                dateIssued: `${formData.dateIssued}T00:00:00.000Z`,
                dueDate: `${formData.dueDate}T00:00:00.000Z`,
            };
            if (editing) {
                await updateInvoice(newInvoice);
                setMessage('Invoice updated.');
            } else {
                await createInvoice(newInvoice);
                setMessage('Invoice created.');
            }
            resetForm();
            await loadInvoices();
        } catch (error) {
            setError(error.response?.data?.message || 'Could not save the invoice. Check the details and try again.');
        } finally {
            setSaving(false);
        }
    }

    function resetForm() {
        setFormData({
            customerName: '',
            description: '',
            total: '',
            paymentStatus: 'Pending',
            dateIssued: new Date().toISOString().slice(0, 10),
            dueDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
        });
        setEditing(false);
        setEditingInvoiceId(null);
    }

    function startEditing(invoice) {
        setFormData({
            customerName: invoice.customerName || '',
            description: invoice.description || '',
            total: String(invoice.total),
            paymentStatus: invoice.paymentStatus,
            dateIssued: new Date(invoice.dateIssued).toISOString().slice(0, 10),
            dueDate: invoice.dueDate
                ? new Date(invoice.dueDate).toISOString().slice(0, 10)
                : new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
        });
        setEditing(true);
        setEditingInvoiceId(invoice.invoiceId);
        setMessage('');
        setError('');
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    async function handleDelete(invoiceId) {
        if (!window.confirm(`Delete invoice ${invoiceId}?`)) return;
        setError('');
        setMessage('');
        try {
            await deleteInvoice(invoiceId);
            if (editingInvoiceId === invoiceId) resetForm();
            setMessage('Invoice deleted.');
            await loadInvoices();
        } catch (error) {
            setError('Could not delete the invoice. Please try again.');
        }
    }

    const filteredInvoices = invoices.filter(invoice =>
        [invoice.invoiceId, invoice.customerName, invoice.description, invoice.paymentStatus].some(value =>
            String(value ?? '').toLowerCase().includes(search.toLowerCase())
        )
    );

    return (
        <div className="invoices-container">
            <header className="page-header">
                <h1>Invoices</h1>
                <p>Manage customer invoices and payment status</p>
            </header>

            <section className="invoice-form-card">
                <h2>{editing ? 'Edit Invoice' : 'Make an Invoice'}</h2>
                <form className="invoice-form" onSubmit={handleSubmit}>
                    <input
                        name="customerName"
                        placeholder="Customer or company"
                        value={formData.customerName}
                        onChange={handleInputChange}
                        required
                    />
                    <input
                        name="description"
                        placeholder="Service description"
                        value={formData.description}
                        onChange={handleInputChange}
                        required
                    />
                    <input 
                        name="total" 
                        type="number" 
                        placeholder="Total Amount" 
                        min="0.01"
                        step="0.01"
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
                        <option value="Overdue">Overdue</option>
                    </select>
                    <label>Issue date
                        <input
                            name="dateIssued"
                            type="date"
                            value={formData.dateIssued}
                            onChange={handleInputChange}
                            required
                        />
                    </label>
                    <label>Due date
                        <input
                            name="dueDate"
                            type="date"
                            value={formData.dueDate}
                            onChange={handleInputChange}
                            required
                        />
                    </label>
                    <div className="invoice-form-actions">
                        <button type="submit" className="save-invoice-btn" disabled={saving}>
                            {saving ? 'Saving...' : editing ? 'Update Invoice' : 'Create Invoice'}
                        </button>
                        {editing && (
                            <button type="button" className="cancel-invoice-btn" onClick={resetForm} aria-label="Cancel editing">
                                <X size={16} /> Cancel
                            </button>
                        )}
                    </div>
                </form>
            </section>

            {error && <p className="invoice-message invoice-error" role="alert">{error}</p>}
            {message && <p className="invoice-message invoice-success" role="status">{message}</p>}

            <div className="invoices-toolbar">
                <input
                    className="search-input"
                    type="search"
                    placeholder="Search invoices"
                    value={search}
                    onChange={event => setSearch(event.target.value)}
                    aria-label="Search invoices"
                />
                <button type="button" className="refresh-invoices-btn" onClick={loadInvoices} disabled={loading} aria-label="Refresh invoices" title="Refresh invoices">
                    <RefreshCw size={16} />
                </button>
            </div>

            <div className="invoices-list-container">
                <table className="invoices-table">
                    <thead>
                        <tr>
                            <th>Invoice ID</th>
                            <th>Customer</th>
                            <th>Description</th>
                            <th>Total</th>
                            <th>Status</th>
                            <th>Issued</th>
                            <th>Due</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {loading ? (
                            <tr><td className="empty-state" colSpan="8">Loading invoices...</td></tr>
                        ) : filteredInvoices.length === 0 ? (
                            <tr><td className="empty-state" colSpan="8">{invoices.length ? 'No matching invoices.' : 'No invoices yet.'}</td></tr>
                        ) : (
                            filteredInvoices.map(invoice => (
                                <tr key={invoice.invoiceId}>
                                    <td>{invoice.invoiceId}</td>
                                    <td>{invoice.customerName || '—'}</td>
                                    <td>{invoice.description || '—'}</td>
                                    <td>{Number(invoice.total).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</td>
                                    <td>{invoice.paymentStatus}</td>
                                    <td>{invoice.dateIssued ? new Date(invoice.dateIssued).toLocaleDateString() : '—'}</td>
                                    <td>{invoice.dueDate ? new Date(invoice.dueDate).toLocaleDateString() : '—'}</td>
                                    <td className="invoice-row-actions">
                                        <button type="button" onClick={() => startEditing(invoice)} aria-label={`Edit ${invoice.invoiceId}`} title="Edit invoice">
                                            <Pencil size={16} />
                                        </button>
                                        <button type="button" onClick={() => handleDelete(invoice.invoiceId)} aria-label={`Delete ${invoice.invoiceId}`} title="Delete invoice">
                                            <Trash2 size={16} />
                                        </button>
                                    </td>
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