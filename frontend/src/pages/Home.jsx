import React, {useEffect, useState} from 'react';
import {Truck, Package, Building2, FileText, Plus, Search, ArrowRight} from 'lucide-react';
import {getAllShipments} from "../services/ShipmentService.js";
import {getShipmentAnalytics} from "../services/ShipmentAnalytics.js";

const Home = ({setActiveTab}) => {
  const modules = [
    {id: 'shipments', title: 'Shipments', description: 'Create and manage delivery shipments.', icon: Truck},
    {id: 'inventory', title: 'Inventory', description: 'Keep track of stock and available goods.', icon: Package},
    {id: 'companies', title: 'Companies', description: 'Manage partners, customers and suppliers.', icon: Building2},
    {id: 'invoices', title: 'Invoices', description: 'Review and manage shipment invoices.', icon: FileText},
  ];

  const [shipments, setShipments] = useState([]);

  useEffect(() => {
    const loadShipments = async () => {
      try {
        const response = await getAllShipments();
        setShipments(response.data);
      } catch (error) {
        console.error('Failed to load shipments:', error);
      }
    };

    loadShipments();
  }, []);

  const {
    totalShipments,
    mostCommonRoute,
    mostCommonRouteCount
  } = getShipmentAnalytics(shipments);

  return (
    <div className="p-8 space-y-8">
      <section className="bg-lms-navy rounded-xl p-12 text-white">
        <div className="flex items-center gap-2 text-sm font-semibold text-lms-ice">
          <Truck className="w-5 h-5" />
          <span>LMS Platform</span>
        </div>
        <h1 className="mt-6 max-w-2xl text-4xl font-bold">Welcome to the Logistic Management System</h1>
        <p className="mt-4 text-lms-ice">Manage companies, inventory, shipments and invoices in one place.</p>
        <div className="mt-8 flex flex-wrap gap-3">
          <button
            type="button"
            onClick={() => setActiveTab('shipments')}
            className="flex min-h-11 items-center gap-2 rounded-md bg-lms-action px-5 font-semibold text-white"
          >
            <Plus className="w-5 h-5" />
            Create Shipment
          </button>
          <button
            type="button"
            onClick={() => setActiveTab('tracking')}
            className="flex min-h-11 items-center gap-2 rounded-md bg-white px-5 font-semibold text-lms-navy"
          >
            <Search className="w-5 h-5" />
            Track Shipment
          </button>
        </div>
      </section>

      <section className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4">
        {modules.map((module) => {
          const Icon = module.icon;
          return (
            <button
              key={module.id}
              type="button"
              onClick={() => setActiveTab(module.id)}
              className="bg-white p-6 rounded-lg border border-slate-200 shadow-sm text-left transition hover:bg-lms-ice/40"
            >
              <span className="mb-5 flex h-11 w-11 items-center justify-center rounded-md bg-lms-ice text-lms-navy">
                <Icon className="w-5 h-5" />
              </span>
              <span className="block font-bold text-lms-textMain">{module.title}</span>
              <span className="mt-2 block text-sm text-lms-textMuted">{module.description}</span>
              <span className="mt-5 flex min-h-11 items-center gap-2 font-semibold text-lms-action">
                Open <ArrowRight className="w-4 h-4" />
              </span>
            </button>
          );
        })}
      </section>

      <section className="bg-white p-6 rounded-lg border border-slate-200 shadow-sm">
        <h2 className="mb-5 text-lg font-bold text-lms-textMain">
          Shipment Overview
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

          <div>
            <p className="text-sm font-semibold text-lms-textMuted">
              Total Shipments
            </p>

            <p className="mt-2 text-3xl font-bold text-lms-navy">
              {totalShipments}
            </p>
          </div>

          <div>
            <p className="text-sm font-semibold text-lms-textMuted">
              Most Common Route
            </p>

            <p className="mt-2 text-xl font-bold text-lms-navy">
              {mostCommonRoute}
            </p>

            <p className="mt-1 text-sm text-lms-textMuted">
              {mostCommonRouteCount} shipment{mostCommonRouteCount !== 1 ? 's' : ''}
            </p>
          </div>

        </div>
      </section>

      <section className="bg-white p-6 rounded-lg border border-slate-200 shadow-sm">
        <h2 className="mb-5 text-lg font-bold text-lms-textMain">How shipments flow through the system</h2>
        <div className="grid grid-cols-1 gap-3 md:grid-cols-6">
          {['Company', 'Inventory', 'Contract', 'Shipment', 'Tracking', 'Invoice'].map((step) => (
            <div key={step} className="flex min-h-12 items-center justify-center rounded-md bg-lms-bg px-3 text-center font-semibold text-lms-navy">
              {step}
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};

export default Home;
