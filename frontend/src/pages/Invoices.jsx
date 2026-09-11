import React from 'react';
import './Invoices.css';

const Invoices = () => {
  return (
    <div className="invoices-container">
      <header className="page-header">
        <h1>Invoices</h1>
        <p>Manage and view all your shipping invoices</p>
      </header>

      <div className="invoices-controls">
        <input 
          type="text" 
          placeholder="Search invoices..." 
          className="search-input"
        />
        <button className="filter-btn">Filter</button>
      </div>

      <div className="invoices-list-container">
        <table className="invoices-table">
          <thead>
            <tr>
              <th>Invoice ID</th>
              <th>Date</th>
              <th>Amount</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td colSpan="5" className="empty-state">No invoices found.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Invoices;
