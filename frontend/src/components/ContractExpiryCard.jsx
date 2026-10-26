import React, {useEffect, useState} from 'react';
import {FileSignature, ArrowRight} from 'lucide-react';
import {getAllContracts} from '../services/ContractService.js';
import {getContractAnalytics} from '../services/ContractAnalytics.js';

// Dashboard widget: shows how many contracts are active, expiring soon or expired.
const ContractExpiryCard = ({setActiveTab}) => {
  const [contracts, setContracts] = useState([]);

  useEffect(() => {
    getAllContracts()
        .then(res => setContracts(Array.isArray(res.data) ? res.data : []))
        .catch(err => console.error('Failed to load contracts:', err));
  }, []);

  const {total, active, expiringSoon, expired, nextToExpire} = getContractAnalytics(contracts);

  const stats = [
    {label: 'Active', value: active, color: 'text-lms-success'},
    {label: 'Expiring in 30 days', value: expiringSoon, color: 'text-amber-600'},
    {label: 'Expired', value: expired, color: 'text-lms-danger'},
  ];

  return (
    <button
      type="button"
      onClick={() => setActiveTab('contracts')}
      className="w-full rounded-lg border border-slate-200 bg-white p-6 text-left shadow-sm transition hover:bg-lms-ice/40"
    >
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm font-semibold text-lms-textMuted">Shipping Contracts</p>
          <p className="mt-2 text-3xl font-bold text-lms-navy">{total}</p>
          <p className="mt-1 text-sm text-lms-textMuted">
            {nextToExpire
                ? `Next to expire: ${nextToExpire.contractNumber} on ${nextToExpire.contractExpiryDate}`
                : 'No upcoming expiries'}
          </p>
        </div>
        <span className="flex h-11 w-11 items-center justify-center rounded-md bg-lms-ice text-lms-navy">
          <FileSignature className="w-5 h-5" />
        </span>
      </div>

      <div className="mt-5 grid grid-cols-3 gap-4">
        {stats.map((s) => (
          <div key={s.label} className="rounded-lg border border-slate-200 bg-slate-50 p-4">
            <p className="text-xs font-semibold text-lms-textMuted">{s.label}</p>
            <p className={`mt-1 text-2xl font-bold ${s.color}`}>{s.value}</p>
          </div>
        ))}
      </div>

      <span className="mt-5 flex items-center gap-2 font-semibold text-lms-action">
        Manage contracts <ArrowRight className="w-4 h-4" />
      </span>
    </button>
  );
};

export default ContractExpiryCard;
