/* 24/08/2026
 *Companies.jsx
 *Companies in pages folder
 * Maghdie Petersen 230600204
 *  */

import React, {useState, useEffect} from 'react';
import {getAllCompanies, createCompany, updateCompany, deleteCompany, getCompanyById} from '../services/CompanyService.js';
import {Building2, Plus, Trash2, Edit3, Eye, Search, Mail, Phone, Hash, X} from 'lucide-react';

const Companies = () => {
  const [companies, setCompanies] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState({companyName: '', taxId: '', phoneNumber: '', emailAddress: ''});
  const [editingCompany, setEditingCompany] = useState(null);
  const [viewingCompany, setViewingCompany] = useState(null);

  useEffect(() => {loadCompanies(); }, []);

  const loadCompanies = () => {
    getAllCompanies().then(res => setCompanies(res.data))
        .catch(err => console.error("API Error:", err));
  };

  const handleInputChange = (e) => {
    const {name, value} = e.target;
    if (editingCompany){
      setEditingCompany({...editingCompany, [name] : value });
    }else {
      setFormData({...formData, [name] : value });
    }
  };

  const handleCreateSubmit = (e) => {
   e.preventDefault();
   createCompany(formData).then(() => {
     loadCompanies();
     setFormData({companyName: '', taxId: '', phoneNumber: '', emailAddress: ''})
   });
  };

  const handleUpdateSubmit = (e) => {
    e.preventDefault();
    updateCompany(editingCompany).then(() => {
      loadCompanies();
      setEditingCompany(null);
    });
  };

  const handleDelete = (id) => {
    if (window.confirm("Delete this company record?")){
      deleteCompany(id).then(() => loadCompanies());
    }
  };

  const filteredCompanies = companies.filter(c => c.companyName?.toLowerCase()
      .includes(searchTerm.toLowerCase()) || c.taxId?.toLowerCase().includes(searchTerm.toLowerCase()));

  return (
    <div className="p-8">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Form */}
        <div className="bg-white p-6 rounded-lg border border-slate-200 shadow-sm h-fit">
          <h2 className="text-lg font-bold text-lms-textMain mb-4 flex items-center gap-2">
            <Plus className="w-5 h-5 text-lms-action" /> Add Company
          </h2>
          <form onSubmit={handleCreateSubmit} className="space-y-4">
            <input type="text" name="companyName" placeholder="Company Name" value={formData.companyName} onChange={handleInputChange} className="w-full p-2.5 border rounded text-sm" required/>
            <input type="text" name="taxId" placeholder="Tax ID" value={formData.taxId} onChange={handleInputChange} className="w-full p-2.5 border rounded text-sm" required/>
            <input type="text" name="phoneNumber" placeholder="Phone Number" value={formData.phoneNumber} onChange={handleInputChange} className="w-full p-2.5 border rounded text-sm" required/>
            <input type="email" name="emailAddress" placeholder="Email Address" value={formData.emailAddress} onChange={handleInputChange} className="w-full p-2.5 border rounded text-sm" required/>
            <button type="submit" className="w-full bg-lms-action text-white py-2.5 rounded text-sm font-medium">Save Company</button>
          </form>
        </div>

        {/* Table */}
        <div className="lg:col-span-2 bg-white p-6 rounded-lg border border-slate-200 shadow-sm">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-lg font-bold text-lms-textMain">Registered Companies</h2>
            <input type="text" placeholder="Search..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="p-2 border rounded text-sm w-64" />
          </div>
          <table className="w-full text-left border-collapse">
            <thead>
            <tr className="border-b bg-slate-50 text-xs font-semibold text-lms-textMuted uppercase">
              <th className="p-3">Name</th>
              <th className="p-3">Tax ID</th>
              <th className="p-3">Contact</th>
              <th className="p-3 text-right">Actions</th>
            </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-sm">
            {filteredCompanies.map((c) => (
                <tr key={c.companyId} className="hover:bg-lms-ice/40">
                  <td className="p-3 font-semibold">{c.companyName}</td>
                  <td className="p-3 text-lms-textMuted">{c.taxId}</td>
                  <td className="p-3 text-xs text-lms-textMuted">{c.emailAddress} | {c.phoneNumber}</td>
                  <td className="p-3 text-right space-x-1">
                    <button onClick={() => getCompanyById(c.companyId).then(res => setViewingCompany(res.data))} className="text-lms-action p-1.5"><Eye className="w-4 h-4" /> </button>
                    <button onClick={() => setEditingCompany(c)} className="text-amber-600 p-1.5"><Edit3 className="w-4 h-4"/> </button>
                    <button onClick={() => handleDelete(c.companyId)} className="text-lms-danger p-1.5"><Trash2 className="w-4 h-4"/> </button>
                  </td>
                </tr>
            ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Edit Modal */}
      {editingCompany &&(
          <div className="fixed inset-0 bg-black/40 flex items-center justify-center p-4">
            <div className="bg-white rounded-lg p-6 max-w-md w-full">
              <h3 className="text-lg font-bold mb-4">Edit Company</h3>
              <form onSubmit={handleUpdateSubmit} className="space-y-3">
                <input type="text" name="companyName" value={editingCompany.companyName} onChange={handleInputChange} className="w-full p-2 border rounded text-sm" required/>
                <input type="text" name="taxId" value={editingCompany.taxId} onChange={handleInputChange} className="w-full p-2 border rounded text-sm" required/>
                <input type="text" name="phoneNumber" value={editingCompany.phoneNumber} onChange={handleInputChange} className="w-full p-2 border rounded text-sm" required/>
                <input type="email" name="emailAddress" value={editingCompany.emailAddress} onChange={handleInputChange} className="w-full p-2 border rounded text-sm" required/>
                <div className="flex gap-2 pt-2">
                  <button type="button" onClick={()=> setEditingCompany(null)} className="w-1/2 py-2 border rounded text-sm">Cancel</button>
                  <button type="submit" className="w-1/2 py-2 bg-lms-action text-white rounded text-sm">Update</button>
                </div>
              </form>
            </div>
          </div>
      )}

      {/* View Details Modal */}
      {viewingCompany &&(
          <div className="fixed inset-0 bg-black/40 flex items-center justify-center p-4">
            <div className="bg-white rounded-lg p-6 max-w-md w-full">
              <h3 className="text-lg font-bold mb-4">Company Details</h3>
              <div className="space-y-2 text-sm">
                <p><strong>ID:</strong> {viewingCompany.companyId}</p>
                <p><strong>Name:</strong> {viewingCompany.companyName}</p>
                <p><strong>Tax ID:</strong> {viewingCompany.taxId}</p>
                <p><strong>Phone:</strong> {viewingCompany.phoneNumber}</p>
                <p><strong>Email:</strong> {viewingCompany.emailAddress}</p>
              </div>
              <button onClick={() => setViewingCompany(null)} className="w-full mt-4 py-2 bg-slate-100 rounded text-sm font-medium">Close</button>
            </div>
          </div>
      )}
    </div>
  );
};

export default Companies;
