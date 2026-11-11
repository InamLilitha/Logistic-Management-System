import React, {useState, useEffect} from 'react';
import {getAllContracts, createContract, updateContract, deleteContract} from '../services/ContractService.js';
import {getContractStatus} from '../services/ContractAnalytics.js';
import {Plus, Trash2, Edit3} from 'lucide-react';

const emptyForm = {contractNumber: '', pricePerKg: '', contractStartDate: '', contractExpiryDate: ''};

// Colour of the status badge for each status.
const badgeStyle = {
  'Active': 'bg-green-50 text-lms-success',
  'Expiring Soon': 'bg-amber-50 text-amber-700',
  'Expired': 'bg-red-50 text-lms-danger',
};

const Contracts = ({ canEdit = true }) => {
  const [contracts, setContracts] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState(emptyForm);
  const [editingContract, setEditingContract] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => { loadContracts(); }, []);

  const loadContracts = () => {
    getAllContracts().then(res => setContracts(res.data))
        .catch(err => console.error('API Error:', err));
  };

  const handleInputChange = (e) => {
    const {name, value} = e.target;
    if (editingContract) {
      setEditingContract({...editingContract, [name]: value});
    } else {
      setFormData({...formData, [name]: value});
    }
  };

  // The backend does not generate the id, so we create one here.
  const handleCreateSubmit = (e) => {
    e.preventDefault();
    if (formData.contractStartDate > formData.contractExpiryDate) {
      setError('Start date cannot be after the expiry date.');
      return;
    }
    setError('');
    createContract({
      contractId: 'CON-' + Date.now(),
      contractNumber: formData.contractNumber,
      pricePerKg: Number(formData.pricePerKg),
      contractStartDate: formData.contractStartDate,
      contractExpiryDate: formData.contractExpiryDate,
    }).then(() => {
      loadContracts();
      setFormData(emptyForm);
    }).catch(() => setError('Could not save the contract. Is the backend running?'));
  };

  const handleUpdateSubmit = (e) => {
    e.preventDefault();
    if (editingContract.contractStartDate > editingContract.contractExpiryDate) {
      setError('Start date cannot be after the expiry date.');
      return;
    }
    setError('');
    updateContract({
      contractId: editingContract.contractId,
      contractNumber: editingContract.contractNumber,
      pricePerKg: Number(editingContract.pricePerKg),
      contractStartDate: editingContract.contractStartDate,
      contractExpiryDate: editingContract.contractExpiryDate,
    }).then(() => {
      loadContracts();
      setEditingContract(null);
    }).catch(() => setError('Could not update the contract.'));
  };

  const handleDelete = (id) => {
    if (window.confirm('Delete this contract?')) {
      deleteContract(id)
          .then(() => loadContracts())
          .catch(() => alert('This contract could not be deleted. It may be linked to a shipment.'));
    }
  };

  const filteredContracts = contracts.filter(c =>
      c.contractNumber?.toLowerCase().includes(searchTerm.toLowerCase()));

  return (
    <div className="p-8">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Form */}
        {canEdit && <div className="bg-white p-6 rounded-lg border border-slate-200 shadow-sm h-fit">
          <h2 className="text-lg font-bold text-lms-textMain mb-4 flex items-center gap-2">
            <Plus className="w-5 h-5 text-lms-action" /> Add Contract
          </h2>
          <form onSubmit={handleCreateSubmit} className="space-y-4">
            <input type="text" name="contractNumber" placeholder="Contract Number" value={formData.contractNumber} onChange={handleInputChange} className="w-full p-2.5 border rounded text-sm" required/>
            <input type="number" name="pricePerKg" placeholder="Price per kg (R)" min="0.01" step="0.01" value={formData.pricePerKg} onChange={handleInputChange} className="w-full p-2.5 border rounded text-sm" required/>
            <label className="block text-xs font-semibold text-lms-textMuted">Start date
              <input type="date" name="contractStartDate" value={formData.contractStartDate} onChange={handleInputChange} className="mt-1 w-full p-2.5 border rounded text-sm font-normal" required/>
            </label>
            <label className="block text-xs font-semibold text-lms-textMuted">Expiry date
              <input type="date" name="contractExpiryDate" value={formData.contractExpiryDate} onChange={handleInputChange} className="mt-1 w-full p-2.5 border rounded text-sm font-normal" required/>
            </label>
            {error && !editingContract && <p className="text-sm text-lms-danger">{error}</p>}
            <button type="submit" className="w-full bg-lms-action text-white py-2.5 rounded text-sm font-medium">Save Contract</button>
          </form>
        </div>}

        {/* Table */}
        <div className="lg:col-span-2 bg-white p-6 rounded-lg border border-slate-200 shadow-sm">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-lg font-bold text-lms-textMain">Shipping Contracts</h2>
            <input type="text" placeholder="Search contract no..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="p-2 border rounded text-sm w-64" />
          </div>
          <table className="w-full text-left border-collapse">
            <thead>
            <tr className="border-b bg-slate-50 text-xs font-semibold text-lms-textMuted uppercase">
              <th className="p-3">Contract No.</th>
              <th className="p-3">Price / kg</th>
              <th className="p-3">Start</th>
              <th className="p-3">Expiry</th>
              <th className="p-3">Status</th>
              <th className="p-3 text-right">Actions</th>
            </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-sm">
            {filteredContracts.map((c) => {
              const status = getContractStatus(c);
              return (
                <tr key={c.contractId} className="hover:bg-lms-ice/40">
                  <td className="p-3 font-semibold">{c.contractNumber}</td>
                  <td className="p-3 text-lms-textMuted">R {Number(c.pricePerKg).toFixed(2)}</td>
                  <td className="p-3 text-lms-textMuted">{c.contractStartDate}</td>
                  <td className="p-3 text-lms-textMuted">{c.contractExpiryDate}</td>
                  <td className="p-3">
                    <span className={`px-2.5 py-1 rounded-full text-xs font-semibold ${badgeStyle[status]}`}>{status}</span>
                  </td>
                  <td className="p-3 text-right space-x-1">
                    {canEdit && <button onClick={() => { setError(''); setEditingContract(c); }} className="text-amber-600 p-1.5"><Edit3 className="w-4 h-4"/></button>}
                    {canEdit && <button onClick={() => handleDelete(c.contractId)} className="text-lms-danger p-1.5"><Trash2 className="w-4 h-4"/></button>}
                  </td>
                </tr>
              );
            })}
            </tbody>
          </table>
          {filteredContracts.length === 0 && (
            <p className="p-6 text-center text-sm text-lms-textMuted">No contracts found.</p>
          )}
        </div>
      </div>

      {/* Edit Modal */}
      {canEdit && editingContract && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center p-4">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h3 className="text-lg font-bold mb-4">Edit Contract</h3>
            <form onSubmit={handleUpdateSubmit} className="space-y-3">
              <input type="text" name="contractNumber" value={editingContract.contractNumber} onChange={handleInputChange} className="w-full p-2 border rounded text-sm" required/>
              <input type="number" name="pricePerKg" min="0.01" step="0.01" value={editingContract.pricePerKg} onChange={handleInputChange} className="w-full p-2 border rounded text-sm" required/>
              <input type="date" name="contractStartDate" value={editingContract.contractStartDate} onChange={handleInputChange} className="w-full p-2 border rounded text-sm" required/>
              <input type="date" name="contractExpiryDate" value={editingContract.contractExpiryDate} onChange={handleInputChange} className="w-full p-2 border rounded text-sm" required/>
              {error && <p className="text-sm text-lms-danger">{error}</p>}
              <div className="flex gap-2 pt-2">
                <button type="button" onClick={() => setEditingContract(null)} className="w-1/2 py-2 border rounded text-sm">Cancel</button>
                <button type="submit" className="w-1/2 py-2 bg-lms-action text-white rounded text-sm">Update</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Contracts;
