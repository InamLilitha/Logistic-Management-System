import React, { useEffect, useMemo, useState } from 'react';
import { AlertCircle, ArrowRight, Package, Pencil, Plus, Search, Trash2, X } from 'lucide-react';
import { getAllCompanies } from '../services/CompanyService.js';
import {
  createInventory,
  deleteInventory,
  getAllInventory,
  updateInventory,
} from '../services/InventoryService.js';

const emptyForm = {
  itemName: '',
  sku: '',
  quantityAvailable: '',
  unitWeight: '',
  companyId: '',
};

const Inventory = ({ setActiveTab }) => {
  const [inventory, setInventory] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [formData, setFormData] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [companyError, setCompanyError] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    loadInventory();
    loadCompanies();
  }, []);

  async function loadInventory() {
    setLoading(true);
    setError('');
    try {
      const response = await getAllInventory();
      setInventory(Array.isArray(response.data) ? response.data : []);
    } catch (loadError) {
      setError('Could not load inventory. Check the server connection and try again.');
    } finally {
      setLoading(false);
    }
  }

  async function loadCompanies() {
    setCompanyError('');
    try {
      const response = await getAllCompanies();
      setCompanies(Array.isArray(response.data) ? response.data : []);
    } catch (loadError) {
      setCompanyError('Company records could not be loaded. Inventory can still be viewed, but you cannot save items until the connection is restored.');
    }
  }

  const companyNames = useMemo(
    () => new Map(companies.map(company => [company.companyId, company.companyName])),
    [companies]
  );

  const filteredInventory = useMemo(() => {
    const query = searchTerm.trim().toLowerCase();
    return inventory.filter(item =>
      [
        item.itemName,
        item.sku,
        item.companyId,
        companyNames.get(item.companyId),
      ].some(value => String(value ?? '').toLowerCase().includes(query))
    );
  }, [inventory, searchTerm, companyNames]);

  const totalUnits = inventory.reduce(
    (total, item) => total + (Number(item.quantityAvailable) || 0),
    0
  );
  const lowStockCount = inventory.filter(item => Number(item.quantityAvailable) <= 10).length;

  function resetForm() {
    setFormData(emptyForm);
    setEditingId(null);
  }

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData(current => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSaving(true);
    setError('');
    setMessage('');

    const item = {
      ...(editingId ? { inventoryId: editingId } : {}),
      itemName: formData.itemName.trim(),
      sku: formData.sku.trim(),
      quantityAvailable: Number(formData.quantityAvailable),
      unitWeight: Number(formData.unitWeight),
      companyId: formData.companyId,
    };

    try {
      if (editingId) {
        await updateInventory(item);
        setMessage('Inventory item updated.');
      } else {
        await createInventory(item);
        setMessage('Inventory item created.');
      }
      resetForm();
      await loadInventory();
    } catch (saveError) {
      setError(
        saveError.response?.data?.message
          || `Could not ${editingId ? 'update' : 'create'} the inventory item. Check the details and try again.`
      );
    } finally {
      setSaving(false);
    }
  }

  function handleEdit(item) {
    setEditingId(item.inventoryId);
    setFormData({
      itemName: item.itemName ?? '',
      sku: item.sku ?? '',
      quantityAvailable: String(item.quantityAvailable ?? ''),
      unitWeight: String(item.unitWeight ?? ''),
      companyId: item.companyId ?? '',
    });
    setError('');
    setMessage('');
  }

  async function handleDelete(item) {
    if (!window.confirm(`Delete ${item.itemName}?`)) return;
    setError('');
    setMessage('');
    try {
      await deleteInventory(item.inventoryId);
      if (editingId === item.inventoryId) resetForm();
      setMessage('Inventory item deleted.');
      await loadInventory();
    } catch (deleteError) {
      setError('Could not delete this inventory item. It may be in use or the server may be unavailable.');
    }
  }

  return (
    <div className="space-y-6 p-6 md:p-8">
      <header className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="text-2xl font-bold text-lms-textMain">Inventory</h1>
          <p className="mt-1 text-sm text-lms-textMuted">Manage stock and link each item to its owning company.</p>
        </div>
        {setActiveTab && (
          <button
            type="button"
            onClick={() => setActiveTab('companies')}
            className="inline-flex min-h-10 items-center gap-2 self-start rounded-md border border-slate-300 bg-white px-4 text-sm font-medium text-lms-textMain hover:bg-slate-50"
          >
            Manage companies <ArrowRight className="h-4 w-4" />
          </button>
        )}
      </header>

      {(error || message || companyError) && (
        <div className="space-y-2" aria-live="polite">
          {error && (
            <p role="alert" className="flex items-center gap-2 rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-700">
              <AlertCircle className="h-4 w-4 shrink-0" />{error}
            </p>
          )}
          {companyError && (
            <p role="alert" className="flex items-center gap-2 rounded-md border border-amber-200 bg-amber-50 p-3 text-sm text-amber-800">
              <AlertCircle className="h-4 w-4 shrink-0" />{companyError}
            </p>
          )}
          {message && (
            <p role="status" className="rounded-md border border-green-200 bg-green-50 p-3 text-sm text-green-800">
              {message}
            </p>
          )}
        </div>
      )}

      <section className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <p className="text-sm text-lms-textMuted">Inventory items</p>
          <p className="mt-2 text-2xl font-bold text-lms-textMain">{inventory.length}</p>
        </div>
        <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <p className="text-sm text-lms-textMuted">Units in stock</p>
          <p className="mt-2 text-2xl font-bold text-lms-textMain">{totalUnits}</p>
        </div>
        <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <p className="text-sm text-lms-textMuted">Low stock (10 or fewer)</p>
          <p className="mt-2 text-2xl font-bold text-amber-700">{lowStockCount}</p>
        </div>
      </section>

      <div className="grid grid-cols-1 items-start gap-6 xl:grid-cols-3">
        <section className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm xl:col-span-1">
          <div className="mb-5 flex items-center gap-2">
            {editingId ? <Pencil className="h-5 w-5 text-lms-action" /> : <Plus className="h-5 w-5 text-lms-action" />}
            <h2 className="text-lg font-bold text-lms-textMain">{editingId ? 'Edit item' : 'Add inventory item'}</h2>
          </div>
          <form onSubmit={handleSubmit} className="space-y-4">
            <label className="block text-sm font-medium text-lms-textMain" htmlFor="itemName">
              Item name
              <input
                id="itemName"
                name="itemName"
                value={formData.itemName}
                onChange={handleChange}
                maxLength={100}
                className="mt-1.5 w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                required
              />
            </label>
            <label className="block text-sm font-medium text-lms-textMain" htmlFor="sku">
              SKU
              <input
                id="sku"
                name="sku"
                value={formData.sku}
                onChange={handleChange}
                maxLength={100}
                className="mt-1.5 w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                required
              />
            </label>
            <div className="grid grid-cols-2 gap-3">
              <label className="block text-sm font-medium text-lms-textMain" htmlFor="quantityAvailable">
                Quantity
                <input
                  id="quantityAvailable"
                  name="quantityAvailable"
                  type="number"
                  min={editingId ? '0' : '1'}
                  step="1"
                  value={formData.quantityAvailable}
                  onChange={handleChange}
                  className="mt-1.5 w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                  required
                />
              </label>
              <label className="block text-sm font-medium text-lms-textMain" htmlFor="unitWeight">
                Unit weight (kg)
                <input
                  id="unitWeight"
                  name="unitWeight"
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={formData.unitWeight}
                  onChange={handleChange}
                  className="mt-1.5 w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                  required
                />
              </label>
            </div>
            <label className="block text-sm font-medium text-lms-textMain" htmlFor="companyId">
              Company
              <select
                id="companyId"
                name="companyId"
                value={formData.companyId}
                onChange={handleChange}
                disabled={companies.length === 0 || Boolean(companyError)}
                className="mt-1.5 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm disabled:bg-slate-100"
                required
              >
                <option value="">Select a company</option>
                {companies.map(company => (
                  <option key={company.companyId} value={company.companyId}>{company.companyName}</option>
                ))}
              </select>
              {companies.length === 0 && !companyError && (
                <span className="mt-1 block text-xs font-normal text-lms-textMuted">
                  Add a company before creating inventory.
                </span>
              )}
            </label>
            <div className="flex gap-2 pt-1">
              <button
                type="submit"
                disabled={saving || companies.length === 0 || Boolean(companyError)}
                className="min-h-10 flex-1 rounded-md bg-lms-action px-4 text-sm font-semibold text-white hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {saving ? 'Saving...' : editingId ? 'Save changes' : 'Add item'}
              </button>
              {editingId && (
                <button
                  type="button"
                  onClick={resetForm}
                  className="inline-flex min-h-10 items-center gap-1 rounded-md border border-slate-300 px-3 text-sm font-medium text-lms-textMain"
                >
                  <X className="h-4 w-4" /> Cancel
                </button>
              )}
            </div>
          </form>
        </section>

        <section className="min-w-0 rounded-lg border border-slate-200 bg-white p-5 shadow-sm xl:col-span-2">
          <div className="mb-5 flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
            <h2 className="text-lg font-bold text-lms-textMain">Stock records</h2>
            <label className="relative block w-full sm:max-w-xs">
              <Search className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
              <input
                type="search"
                aria-label="Search inventory"
                placeholder="Search name, SKU, or company..."
                value={searchTerm}
                onChange={event => setSearchTerm(event.target.value)}
                className="w-full rounded-md border border-slate-300 py-2 pl-9 pr-3 text-sm"
              />
            </label>
          </div>
          {loading ? (
            <p className="py-10 text-center text-sm text-lms-textMuted">Loading inventory...</p>
          ) : inventory.length === 0 ? (
            <div className="py-10 text-center">
              <Package className="mx-auto h-8 w-8 text-slate-400" />
              <p className="mt-3 text-sm font-medium text-lms-textMain">No inventory items yet.</p>
              <p className="mt-1 text-sm text-lms-textMuted">Add an item using the form.</p>
            </div>
          ) : filteredInventory.length === 0 ? (
            <p className="py-10 text-center text-sm text-lms-textMuted">No items match your search.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full min-w-[650px] border-collapse text-left">
                <thead>
                  <tr className="border-b bg-slate-50 text-xs uppercase text-lms-textMuted">
                    <th className="p-3 font-semibold">Item</th>
                    <th className="p-3 font-semibold">Company</th>
                    <th className="p-3 text-right font-semibold">Quantity</th>
                    <th className="p-3 text-right font-semibold">Unit weight</th>
                    <th className="p-3 text-right font-semibold">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 text-sm">
                  {filteredInventory.map(item => {
                    const quantity = Number(item.quantityAvailable);
                    return (
                      <tr key={item.inventoryId} className="hover:bg-slate-50">
                        <td className="p-3">
                          <p className="font-semibold text-lms-textMain">{item.itemName}</p>
                          <p className="mt-0.5 text-xs text-lms-textMuted">SKU: {item.sku}</p>
                        </td>
                        <td className="p-3 text-lms-textMuted">
                          {companyNames.get(item.companyId) || 'Company unavailable'}
                        </td>
                        <td className="p-3 text-right">
                          <span className={quantity <= 10 ? 'font-semibold text-amber-700' : 'text-lms-textMain'}>
                            {item.quantityAvailable}
                          </span>
                        </td>
                        <td className="p-3 text-right text-lms-textMuted">{item.unitWeight} kg</td>
                        <td className="p-3 text-right">
                          <div className="inline-flex items-center gap-1">
                            <button
                              type="button"
                              onClick={() => handleEdit(item)}
                              aria-label={`Edit ${item.itemName}`}
                              className="rounded p-2 text-amber-700 hover:bg-amber-50"
                            >
                              <Pencil className="h-4 w-4" />
                            </button>
                            <button
                              type="button"
                              onClick={() => handleDelete(item)}
                              aria-label={`Delete ${item.itemName}`}
                              className="rounded p-2 text-red-600 hover:bg-red-50"
                            >
                              <Trash2 className="h-4 w-4" />
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </div>
    </div>
  );
};

export default Inventory;
