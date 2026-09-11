import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './Invoices.css';

const API_BASE = 'http://localhost:8080/invoices';

const Invoices = () => {
  const [invoices, setInvoices] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState({
    invoiceId: '',
    total: '',
    paymentStatus: 'Pending',
    dateIssued: new Date().toISOString().slice(0, 10),
  });
  const [message, setMessage] = useState('');

  const loadInvoices = () => {
    axios.get(`${API_BASE}/getall`)
      .then((response) => setInvoices(response.data))
      .catch(() => setMessage('Invoices could not be loaded. Check that the backend is running.'));
  };

  useEffect(() => {
    loadInvoices();
  }, []);

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    setMessage('');

    const invoice = {
      ...formData,
      total: Number(formData.total),
      dateIssued: `${formData.dateIssued}T00:00:00.000Z`,
    };

    axios.post(`${API_BASE}/create`, invoice)
      .then(() => {
        setFormData({
          invoiceId: '',
          total: '',
          paymentStatus: 'Pending',
          dateIssued: new Date().toISOString().slice(0, 10),
        });
        setMessage('Invoice saved successfully.');
        loadInvoices();
      })
      .catch(() => setMessage('Invoice could not be saved. Check the details and try again.'));
  };

  const filteredInvoices = invoices.filter((invoice) =>
    invoice.invoiceId?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="invoices-container">
      <header className="page-header">
        <h1>Invoices</h1>
        <p>Create and manage shipping invoices</p>
      </header>

      <section className="invoice-form-card">
        <h2>Make an Invoice</h2>
        <form className="invoice-form" onSubmit={handleSubmit}>
          <label>
            Invoice ID
            <input name="invoiceId" value={formData.invoiceId} onChange={handleInputChange} placeholder="INV-001" required />
          </label>
          <label>
            Total
            <input name="total" type="number" min="0" step="0.01" value={formData.total} onChange={handleInputChange} placeholder="0.00" required />
          </label>
          <label>
            Payment Status
            <select name="paymentStatus" value={formData.paymentStatus} onChange={handleInputChange}>
              <option value="Pending">Pending</option>
              <option value="Paid">Paid</option>
              <option value="Overdue">Overdue</option>
            </select>
          </label>
          <label>
            Date Issued
            <input name="dateIssued" type="date" value={formData.dateIssued} onChange={handleInputChange} required />
          </label>
          <button type="submit" className="save-invoice-btn">Save Invoice</button>
        </form>
        {message && <p className="invoice-message">{message}</p>}
      </section>

      <div className="invoices-controls">
        <input type="search" placeholder="Search by invoice ID" value={searchTerm} onChange={(event) => setSearchTerm(event.target.value)} className="search-input" />
      </div>

      <div className="invoices-list-container">
        <table className="invoices-table">
          <thead>
            <tr>
              <th>Invoice ID</th>
              <th>Total</th>
              <th>Payment Status</th>
              <th>Date Issued</th>
            </tr>
          </thead>
          <tbody>
            {filteredInvoices.length === 0 ? (
              <tr><td colSpan="4" className="empty-state">No invoices found.</td></tr>
            ) : filteredInvoices.map((invoice) => (
              <tr key={invoice.invoiceId}>
                <td>{invoice.invoiceId}</td>
                <td>{Number(invoice.total).toFixed(2)}</td>
                <td>{invoice.paymentStatus}</td>
                <td>{new Date(invoice.dateIssued).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Invoices;
