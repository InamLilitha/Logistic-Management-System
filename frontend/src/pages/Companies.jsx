/*
 * Last worked on: Wednesday 11/11/2026
 * Companies.jsx
 * Companies in pages folder
 * Maghdie Petersen 230600204
 */

import React, { useState, useEffect } from 'react';

import {
    getAllCompanies,
    createCompany,
    updateCompany,
    deleteCompany,
    getCompanyById
} from '../services/CompanyService.js';

import {
    Building2,
    Plus,
    Trash2,
    Edit3,
    Eye,
    Search,
    X
} from 'lucide-react';


const initialFormData = {
    companyName: '',
    companyType: 'Client',
    taxId: '',
    phoneNumber: '',
    emailAddress: ''
};


const Companies = ({ canEdit = true }) => {

    const [companies, setCompanies] = useState([]);

    const [searchTerm, setSearchTerm] = useState('');

    const [formData, setFormData] = useState(initialFormData);

    const [editingCompany, setEditingCompany] = useState(null);

    const [viewingCompany, setViewingCompany] = useState(null);

    const [loading, setLoading] = useState(false);

    const [error, setError] = useState('');

    const [success, setSuccess] = useState('');


    // Load companies when component opens
    useEffect(() => {
        loadCompanies();
    }, []);


    // Fetch all companies
    const loadCompanies = async () => {

        setLoading(true);
        setError('');

        try {

            const response = await getAllCompanies();

            setCompanies(Array.isArray(response.data) ? response.data : []);

        } catch (err) {

            console.error("API Error:", err);

            setError("Unable to load companies. Please try again.");

        } finally {

            setLoading(false);

        }
    };


    // Handle form input changes
    const handleInputChange = (e) => {

        const { name, value } = e.target;

        if (editingCompany) {

            setEditingCompany({
                ...editingCompany,
                [name]: value
            });

        } else {

            setFormData({
                ...formData,
                [name]: value
            });

        }
    };


    // Create company
    const handleCreateSubmit = async (e) => {

        e.preventDefault();

        setError('');
        setSuccess('');

        try {

            await createCompany(formData);

            setSuccess("Company successfully created.");

            setFormData(initialFormData);

            await loadCompanies();

        } catch (err) {

            console.error("Create Error:", err);

            setError(
                err.response?.data?.message ||
                "Unable to create company. Please check your information."
            );

        }
    };


    // Update company
    const handleUpdateSubmit = async (e) => {

        e.preventDefault();

        setError('');
        setSuccess('');

        try {

            await updateCompany(
                editingCompany.companyId,
                editingCompany
            );

            setSuccess("Company successfully updated.");

            setEditingCompany(null);

            await loadCompanies();

        } catch (err) {

            console.error("Update Error:", err);

            setError(
                err.response?.data?.message ||
                "Unable to update company. Please try again."
            );

        }
    };


    // Delete company
    const handleDelete = async (id) => {

        const confirmed = window.confirm(
            "Are you sure you want to delete this company?"
        );

        if (!confirmed) return;

        setError('');
        setSuccess('');

        try {

            await deleteCompany(id);

            setSuccess("Company successfully deleted.");

            await loadCompanies();

        } catch (err) {

            console.error("Delete Error:", err);

            setError(
                err.response?.data?.message ||
                "Unable to delete company. Please try again."
            );

        }
    };


    // View company details
    const handleViewCompany = async (id) => {

        setError('');

        try {

            const response = await getCompanyById(id);

            setViewingCompany(response.data);

        } catch (err) {

            console.error("View Error:", err);

            setError("Unable to retrieve company details.");

        }
    };


    // Filter companies
    const filteredCompanies = companies.filter((company) => {

        const search = searchTerm.toLowerCase();

        return (

            company.companyName?.toLowerCase().includes(search) ||

            company.taxId?.toLowerCase().includes(search) ||

            company.companyType?.toLowerCase().includes(search) ||

            company.emailAddress?.toLowerCase().includes(search)

        );

    });


    return (

        <div className="p-8">

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">


                {/* Add Company Form */}

                {canEdit && (

                    <div className="bg-white p-6 rounded-lg border border-slate-200 shadow-sm h-fit">

                        <h2 className="text-lg font-bold text-lms-textMain mb-4 flex items-center gap-2">

                            <Plus className="w-5 h-5 text-lms-action" />

                            Add Company

                        </h2>


                        <form onSubmit={handleCreateSubmit} className="space-y-4">

                            <input
                                type="text"
                                name="companyName"
                                placeholder="Company Name"
                                value={formData.companyName}
                                onChange={handleInputChange}
                                className="w-full p-2.5 border rounded text-sm"
                                required
                            />


                            <select
                                name="companyType"
                                value={formData.companyType}
                                onChange={handleInputChange}
                                className="w-full p-2.5 border rounded text-sm bg-white"
                                required
                            >

                                <option value="Client">Client</option>

                                <option value="Supplier">Supplier</option>

                                <option value="Carrier">Carrier</option>

                            </select>


                            <input
                                type="text"
                                name="taxId"
                                placeholder="Tax ID"
                                value={formData.taxId}
                                onChange={handleInputChange}
                                className="w-full p-2.5 border rounded text-sm"
                                required
                            />


                            <input
                                type="text"
                                name="phoneNumber"
                                placeholder="Phone Number"
                                value={formData.phoneNumber}
                                onChange={handleInputChange}
                                className="w-full p-2.5 border rounded text-sm"
                                required
                            />


                            <input
                                type="email"
                                name="emailAddress"
                                placeholder="Email Address"
                                value={formData.emailAddress}
                                onChange={handleInputChange}
                                className="w-full p-2.5 border rounded text-sm"
                                required
                            />


                            <button
                                type="submit"
                                className="w-full bg-lms-action text-white py-2.5 rounded text-sm font-medium hover:opacity-90"
                            >

                                Save Company

                            </button>

                        </form>

                    </div>

                )}



                {/* Registered Companies Table */}

                <div className={`${canEdit ? 'lg:col-span-2' : 'lg:col-span-3'} bg-white p-6 rounded-lg border border-slate-200 shadow-sm`}>

                    <div className="flex flex-col sm:flex-row justify-between items-center gap-4 mb-6">

                        <h2 className="text-lg font-bold text-lms-textMain flex items-center gap-2">

                            <Building2 className="w-5 h-5" />

                            Registered Companies

                        </h2>


                        <div className="relative">

                            <Search className="absolute left-3 top-2.5 w-4 h-4 text-slate-400" />

                            <input
                                type="text"
                                placeholder="Search companies..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="pl-9 pr-3 py-2 border rounded text-sm w-full sm:w-64"
                            />

                        </div>

                    </div>


                    {/* Messages */}

                    {error && (

                        <div className="mb-4 p-3 bg-red-50 text-red-700 rounded text-sm">

                            {error}

                        </div>

                    )}


                    {success && (

                        <div className="mb-4 p-3 bg-green-50 text-green-700 rounded text-sm">

                            {success}

                        </div>

                    )}



                    {/* Table */}

                    {loading ? (

                        <p className="text-center py-8 text-slate-500">

                            Loading companies...

                        </p>

                    ) : (

                        <div className="overflow-x-auto">

                            <table className="w-full text-left border-collapse">

                                <thead>

                                    <tr className="border-b bg-slate-50 text-xs font-semibold text-lms-textMuted uppercase">

                                        <th className="p-3">Name</th>

                                        <th className="p-3">Type</th>

                                        <th className="p-3">Tax ID</th>

                                        <th className="p-3">Contact</th>

                                        <th className="p-3 text-right">Actions</th>

                                    </tr>

                                </thead>


                                <tbody className="divide-y divide-slate-100 text-sm">

                                    {filteredCompanies.length > 0 ? (

                                        filteredCompanies.map((company) => (

                                            <tr
                                                key={company.companyId}
                                                className="hover:bg-lms-ice/40"
                                            >

                                                <td className="p-3 font-semibold">

                                                    {company.companyName}

                                                </td>


                                                <td className="p-3">

                                                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${
  company.companyType === 'Client'
      ? 'bg-blue-100 text-blue-700'
      : company.companyType === 'Supplier'
          ? 'bg-purple-100 text-purple-700'
          : 'bg-amber-100 text-amber-700'
}`}>

                                                        {company.companyType || 'Client'}

                                                    </span>

                                                </td>


                                                <td className="p-3 text-lms-textMuted">

                                                    {company.taxId}

                                                </td>


                                                <td className="p-3 text-xs text-lms-textMuted">

                                                    {company.emailAddress}

                                                    <br />

                                                    {company.phoneNumber}

                                                </td>


                                                <td className="p-3 text-right whitespace-nowrap">

                                                    <button
                                                        onClick={() => handleViewCompany(company.companyId)}
                                                        className="text-lms-action p-1.5 hover:bg-blue-50 rounded"
                                                        title="View Company"
                                                    >

                                                        <Eye className="w-4 h-4" />

                                                    </button>


                                                    {canEdit && (

                                                        <button
                                                            onClick={() => setEditingCompany({ ...company })}
                                                            className="text-amber-600 p-1.5 hover:bg-amber-50 rounded"
                                                            title="Edit Company"
                                                        >

                                                            <Edit3 className="w-4 h-4" />

                                                        </button>

                                                    )}


                                                    {canEdit && (

                                                        <button
                                                            onClick={() => handleDelete(company.companyId)}
                                                            className="text-lms-danger p-1.5 hover:bg-red-50 rounded"
                                                            title="Delete Company"
                                                        >

                                                            <Trash2 className="w-4 h-4" />

                                                        </button>

                                                    )}

                                                </td>

                                            </tr>

                                        ))

                                    ) : (

                                        <tr>

                                            <td colSpan="5" className="text-center py-8 text-slate-500">

                                                No companies found.

                                            </td>

                                        </tr>

                                    )}

                                </tbody>

                            </table>

                        </div>

                    )}

                </div>

            </div>



            {/* Edit Company Modal */}

            {canEdit && editingCompany && (

                <div className="fixed inset-0 bg-black/40 flex items-center justify-center p-4 z-50">

                    <div className="bg-white rounded-lg p-6 max-w-md w-full shadow-xl">

                        <div className="flex justify-between items-center mb-4">

                            <h3 className="text-lg font-bold">

                                Edit Company

                            </h3>

                            <button
                                type="button"
                                onClick={() => setEditingCompany(null)}
                                className="text-slate-500 hover:text-slate-800"
                            >

                                <X className="w-5 h-5" />

                            </button>

                        </div>


                        <form onSubmit={handleUpdateSubmit} className="space-y-3">

                            <input
                                type="text"
                                name="companyName"
                                placeholder="Company Name"
                                value={editingCompany.companyName || ''}
                                onChange={handleInputChange}
                                className="w-full p-2 border rounded text-sm"
                                required
                            />


                            <select
                                name="companyType"
                                value={editingCompany.companyType || 'Client'}
                                onChange={handleInputChange}
                                className="w-full p-2 border rounded text-sm bg-white"
                                required
                            >

                                <option value="Client">Client</option>

                                <option value="Supplier">Supplier</option>

                                <option value="Carrier">Carrier</option>

                            </select>


                            <input
                                type="text"
                                name="taxId"
                                placeholder="Tax ID"
                                value={editingCompany.taxId || ''}
                                onChange={handleInputChange}
                                className="w-full p-2 border rounded text-sm"
                                required
                            />


                            <input
                                type="text"
                                name="phoneNumber"
                                placeholder="Phone Number"
                                value={editingCompany.phoneNumber || ''}
                                onChange={handleInputChange}
                                className="w-full p-2 border rounded text-sm"
                                required
                            />


                            <input
                                type="email"
                                name="emailAddress"
                                placeholder="Email Address"
                                value={editingCompany.emailAddress || ''}
                                onChange={handleInputChange}
                                className="w-full p-2 border rounded text-sm"
                                required
                            />


                            <div className="flex gap-2 pt-2">

                                <button
                                    type="button"
                                    onClick={() => setEditingCompany(null)}
                                    className="w-1/2 py-2 border rounded text-sm hover:bg-slate-50"
                                >

                                    Cancel

                                </button>


                                <button
                                    type="submit"
                                    className="w-1/2 py-2 bg-lms-action text-white rounded text-sm hover:opacity-90"
                                >

                                    Update

                                </button>

                            </div>

                        </form>

                    </div>

                </div>

            )}



            {/* View Company Details Modal */}

            {viewingCompany && (

                <div className="fixed inset-0 bg-black/40 flex items-center justify-center p-4 z-50">

                    <div className="bg-white rounded-lg p-6 max-w-md w-full shadow-xl">

                        <div className="flex justify-between items-center mb-4">

                            <h3 className="text-lg font-bold">

                                Company Details

                            </h3>


                            <button
                                type="button"
                                onClick={() => setViewingCompany(null)}
                                className="text-slate-500 hover:text-slate-800"
                            >

                                <X className="w-5 h-5" />

                            </button>

                        </div>


                        <div className="space-y-3 text-sm">

                            <p>

                                <strong>ID:</strong>

                                <br />

                                {viewingCompany.companyId}

                            </p>


                            <p>

                                <strong>Name:</strong>

                                <br />

                                {viewingCompany.companyName}

                            </p>


                            <p>

                                <strong>Type:</strong>

                                <br />

                                {viewingCompany.companyType || 'Client'}

                            </p>


                            <p>

                                <strong>Tax ID:</strong>

                                <br />

                                {viewingCompany.taxId}

                            </p>


                            <p>

                                <strong>Phone:</strong>

                                <br />

                                {viewingCompany.phoneNumber}

                            </p>


                            <p>

                                <strong>Email:</strong>

                                <br />

                                {viewingCompany.emailAddress}

                            </p>

                        </div>


                        <button
                            onClick={() => setViewingCompany(null)}
                            className="w-full mt-5 py-2 bg-slate-100 rounded text-sm font-medium hover:bg-slate-200"
                        >

                            Close

                        </button>

                    </div>

                </div>

            )}

        </div>

    );

};


export default Companies;
