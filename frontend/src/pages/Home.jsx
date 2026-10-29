import React, { useEffect, useState } from "react";
import {
  Truck,
  Package,
  Building2,
  FileText,
  Plus,
  Search,
  ArrowRight,
  Clock,
  Activity,
  RefreshCw
} from "lucide-react";

import { getAllShipments } from "../services/ShipmentService.js";
import { getShipmentAnalytics } from "../services/ShipmentAnalytics.js";
import { getShipmentTracking } from "../services/ShipmentTrackingService.js";
import { getInvoices } from "../services/InvoiceService.js";
import { getAllCompanies } from "../services/CompanyService.js";
import { getAllInventory } from "../services/InventoryService.js";
import ContractExpiryCard from "../components/ContractExpiryCard.jsx";

const Home = ({ setActiveTab, canEdit = true }) => {
  const [shipments, setShipments] = useState([]);
  const [trackingRecords, setTrackingRecords] = useState([]);
  const [pendingInvoices, setPendingInvoices] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [inventoryItems, setInventoryItems] = useState([]);

  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState("");

  const loadDashboard = async () => {
    setLoading(true);
    setLoadError("");


const results = await Promise.allSettled([
  getAllShipments(),
  getShipmentTracking(),
  getInvoices(),
  getAllCompanies(),
  getAllInventory()
]);

const errors = [];

const shipmentResult = results[0];

if (shipmentResult.status === "fulfilled") {
  const data = shipmentResult.value?.data;
  setShipments(Array.isArray(data) ? data : []);
} else {
  console.error("Failed to load shipments:", shipmentResult.reason);
  errors.push("shipments");
  setShipments([]);
}

const trackingResult = results[1];

if (trackingResult.status === "fulfilled") {
  const response = trackingResult.value;
  const data = Array.isArray(response) ? response : response?.data;

  setTrackingRecords(Array.isArray(data) ? data : []);
} else {
  console.error("Failed to load tracking records:", trackingResult.reason);
  errors.push("tracking");
  setTrackingRecords([]);
}

const invoiceResult = results[2];

if (invoiceResult.status === "fulfilled") {
  const response = invoiceResult.value;
  const data = Array.isArray(response) ? response : response?.data;

  setPendingInvoices(
    (Array.isArray(data) ? data : []).filter(
      (invoice) => invoice.paymentStatus === "Pending"
    )
  );
} else {
  console.error("Failed to load invoices:", invoiceResult.reason);
  errors.push("invoices");
  setPendingInvoices([]);
}

const companyResult = results[3];

if (companyResult.status === "fulfilled") {
  const response = companyResult.value;
  const data = Array.isArray(response) ? response : response?.data;

  setCompanies(Array.isArray(data) ? data : []);
} else {
  console.error("Failed to load companies:", companyResult.reason);
  errors.push("companies");
  setCompanies([]);
}

const inventoryResult = results[4];

if (inventoryResult.status === "fulfilled") {
  const response = inventoryResult.value;
  const data = Array.isArray(response) ? response : response?.data;

  setInventoryItems(Array.isArray(data) ? data : []);
} else {
  console.error("Failed to load inventory:", inventoryResult.reason);
  errors.push("inventory");
  setInventoryItems([]);
}

if (errors.length > 0) {
  setLoadError(
    `Some dashboard information could not be loaded: ${errors.join(", ")}. Other available information is still displayed.`
  );
}

setLoading(false);


  };

  useEffect(() => {
    loadDashboard();
  }, []);

  const analytics = getShipmentAnalytics(shipments) || {};

  const totalShipments =
      analytics.totalShipments ?? shipments.length;

  const mostCommonRoute =
      analytics.mostCommonRoute || "No routes yet";

  const mostCommonRouteCount =
      analytics.mostCommonRouteCount ?? 0;

  const shipmentStatusAnalytics = {
    total: trackingRecords.length,
    pending: trackingRecords.filter(
        (record) => record.shipmentStatus === "Pending"
    ).length,
    inTransit: trackingRecords.filter(
        (record) => record.shipmentStatus === "In Transit"
    ).length,
    outForDelivery: trackingRecords.filter(
        (record) => record.shipmentStatus === "Out for Delivery"
    ).length,
    delivered: trackingRecords.filter(
        (record) => record.shipmentStatus === "Delivered"
    ).length,
    delayed: trackingRecords.filter(
        (record) => record.shipmentStatus === "Delayed"
    ).length
  };

  const outstandingAmount = pendingInvoices.reduce(
      (total, invoice) => total + (Number(invoice.total) || 0),
      0
  );

  const modules = [
    {
      id: "shipments",
      title: "Shipments",
      description: "Create and manage cargo movements.",
      icon: Truck,
      count: shipments.length
    },
    {
      id: "inventory",
      title: "Inventory",
      description: "Monitor stock and available goods.",
      icon: Package,
      count: inventoryItems.length
    },
    {
      id: "companies",
      title: "Companies",
      description: "Manage customers and business partners.",
      icon: Building2,
      count: companies.length
    },
    {
      id: "invoices",
      title: "Invoices",
      description: "Review and manage shipment invoices.",
      icon: FileText,
      count: null
    }
  ];

  const statCards = [
    {
      title: "Total Shipments",
      value: shipments.length,
      subtitle: "Registered shipments",
      icon: Truck,
      tab: "shipments"
    },
    {
      title: "Companies",
      value: companies.length,
      subtitle: "Registered companies",
      icon: Building2,
      tab: "companies"
    },
    {
      title: "Inventory Items",
      value: inventoryItems.length,
      subtitle: "Recorded inventory entries",
      icon: Package,
      tab: "inventory"
    },
    {
      title: "Pending Invoices",
      value: pendingInvoices.length,
      subtitle: "Awaiting payment",
      icon: Clock,
      tab: "invoices"
    }
  ];

  const statusCards = [
    { label: "Pending", value: shipmentStatusAnalytics.pending },
    { label: "In Transit", value: shipmentStatusAnalytics.inTransit },
    { label: "Out for Delivery", value: shipmentStatusAnalytics.outForDelivery },
    { label: "Delivered", value: shipmentStatusAnalytics.delivered },
    { label: "Delayed", value: shipmentStatusAnalytics.delayed }
  ];

  return ( <div className="space-y-8 p-4 sm:p-6 lg:p-8">
    {/* WELCOME BANNER */} <section className="relative overflow-hidden rounded-2xl bg-lms-navy p-6 text-white shadow-sm sm:p-9 lg:p-12"> <div className="relative z-10 max-w-3xl">
        <div className="mb-5 flex items-center gap-2 text-sm font-semibold text-lms-ice">
          <Truck className="h-5 w-5" />
          <span>LMS Platform</span>
        </div>


    <h1 className="max-w-2xl text-3xl font-bold leading-tight sm:text-4xl">
      Welcome to the Logistic Management System
    </h1>

    <p className="mt-4 max-w-xl text-sm leading-7 text-lms-ice sm:text-base">
      Manage companies, inventory, shipments and invoices in one place.
    </p>

    <div className="mt-7 flex flex-wrap gap-3">
      {canEdit && (
          <button
              type="button"
              onClick={() => setActiveTab("shipments")}
              className="inline-flex min-h-11 items-center gap-2 rounded-lg bg-lms-action px-5 font-semibold text-white transition hover:opacity-90"
          >
            <Plus className="h-5 w-5" />
            Create Shipment
          </button>
      )}

      <button
          type="button"
          onClick={() => setActiveTab("tracking")}
          className="inline-flex min-h-11 items-center gap-2 rounded-lg bg-white px-5 font-semibold text-lms-navy transition hover:bg-slate-100"
      >
        <Search className="h-5 w-5" />
        Track Shipment
      </button>
    </div>
  </div>

    <div
        aria-hidden="true"
        className="pointer-events-none absolute -right-16 -top-20 hidden h-80 w-80 rounded-full border border-white/10 lg:block"
    />

    <div
        aria-hidden="true"
        className="pointer-events-none absolute -right-5 -top-9 hidden h-60 w-60 rounded-full border border-white/10 lg:block"
    />
  </section>

    {/* LOAD ERROR */}
    {loadError && (
        <div
            role="alert"
            className="flex flex-col gap-3 rounded-xl border border-slate-200 bg-white p-4 text-sm text-slate-600 sm:flex-row sm:items-center sm:justify-between"
        >
          <p>{loadError}</p>

          <button
              type="button"
              onClick={loadDashboard}
              disabled={loading}
              className="inline-flex min-h-10 shrink-0 items-center justify-center gap-2 rounded-lg border border-slate-200 px-4 font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-50"
          >
            <RefreshCw className="h-4 w-4" />
            Retry
          </button>
        </div>
    )}

    {/* KPI CARDS */}
    <section>
      <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="text-lg font-bold text-lms-textMain">
            Operational Overview
          </h2>
          <p className="mt-1 text-sm text-lms-textMuted">
            A snapshot of your logistics system.
          </p>
        </div>

        {loading && (
            <span className="text-xs font-medium text-lms-textMuted">
          Updating dashboard...
        </span>
        )}
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {statCards.map((card) => {
          const Icon = card.icon;

          return (
              <button
                  key={card.title}
                  type="button"
                  onClick={() => setActiveTab(card.tab)}
                  className="group rounded-xl border border-slate-200 bg-white p-5 text-left shadow-sm transition hover:-translate-y-0.5 hover:shadow-md sm:p-6"
              >
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <p className="text-sm font-medium text-lms-textMuted">
                      {card.title}
                    </p>

                    <p className="mt-3 text-3xl font-bold tracking-tight text-lms-navy">
                      {loading ? "—" : card.value.toLocaleString("en-ZA")}
                    </p>
                  </div>

                  <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-lms-ice text-lms-navy">
                <Icon className="h-5 w-5" />
              </span>
                </div>

                <div className="mt-4 flex items-center justify-between gap-2">
              <span className="text-xs text-lms-textMuted">
                {card.subtitle}
              </span>

                  <ArrowRight className="h-4 w-4 text-lms-action transition group-hover:translate-x-1" />
                </div>
              </button>
          );
        })}
      </div>
    </section>

    {/* QUICK ACCESS */}
    <section>
      <div className="mb-4">
        <h2 className="text-lg font-bold text-lms-textMain">
          Quick Access
        </h2>
        <p className="mt-1 text-sm text-lms-textMuted">
          Jump directly to a module.
        </p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {modules.map((module) => {
          const Icon = module.icon;

          return (
              <button
                  key={module.id}
                  type="button"
                  onClick={() => setActiveTab(module.id)}
                  className="group rounded-xl border border-slate-200 bg-white p-5 text-left transition hover:border-slate-300 hover:shadow-sm"
              >
                <div className="flex items-start justify-between gap-3">
              <span className="flex h-11 w-11 items-center justify-center rounded-xl bg-lms-ice text-lms-navy">
                <Icon className="h-5 w-5" />
              </span>

                  {module.count !== null && (
                      <span className="rounded-full border border-slate-200 px-3 py-1 text-xs font-semibold text-slate-600">
                  {loading ? "—" : module.count}
                </span>
                  )}
                </div>

                <h3 className="mt-4 font-bold text-lms-textMain">
                  {module.title}
                </h3>

                <p className="mt-2 min-h-10 text-sm leading-5 text-lms-textMuted">
                  {module.description}
                </p>

                <span className="mt-4 inline-flex items-center gap-2 text-sm font-semibold text-lms-action">
              Open module
              <ArrowRight className="h-4 w-4 transition group-hover:translate-x-1" />
            </span>
              </button>
          );
        })}
      </div>
    </section>

    {/* CONTRACT EXPIRY */}
    <ContractExpiryCard setActiveTab={setActiveTab} />

    {/* INVOICE SUMMARY */}
    <button
        type="button"
        onClick={() => setActiveTab("invoices")}
        className="w-full rounded-xl border border-slate-200 bg-white p-5 text-left shadow-sm transition hover:shadow-md sm:p-6"
    >
      <div className="flex flex-col justify-between gap-5 sm:flex-row sm:items-center">
        <div>
          <p className="text-sm font-semibold text-lms-textMuted">
            Outstanding Invoices
          </p>

          <p className="mt-2 text-3xl font-bold text-lms-navy">
            {loading ? "—" : pendingInvoices.length}
          </p>

          <p className="mt-2 text-sm text-lms-textMuted">
            {loading
                ? "Loading outstanding amount..."
                : `R ${outstandingAmount.toLocaleString("en-ZA", {
                  minimumFractionDigits: 2,
                  maximumFractionDigits: 2
                })} awaiting payment`}
          </p>
        </div>

        <div className="flex items-center justify-between gap-3 sm:justify-end">
        <span className="flex h-11 w-11 items-center justify-center rounded-xl bg-lms-ice text-lms-navy">
          <Clock className="h-5 w-5" />
        </span>

          <span className="inline-flex items-center gap-2 text-sm font-semibold text-lms-action">
          Review invoices
          <ArrowRight className="h-4 w-4" />
        </span>
        </div>
      </div>
    </button>

    {/* SHIPMENT ANALYTICS */}
    <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="mb-6 flex items-start gap-3">
      <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-lms-ice text-lms-navy">
        <Truck className="h-5 w-5" />
      </span>

        <div>
          <h2 className="text-lg font-bold text-lms-textMain">
            Shipment Analytics
          </h2>
          <p className="mt-1 text-sm text-lms-textMuted">
            A summary of registered shipment routes.
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
        <div className="rounded-xl border border-slate-200 p-5">
          <p className="text-sm font-medium text-lms-textMuted">
            Total Shipments
          </p>

          <p className="mt-2 text-3xl font-bold text-lms-navy">
            {loading ? "—" : totalShipments}
          </p>

          <button
              type="button"
              onClick={() => setActiveTab("shipments")}
              className="mt-4 inline-flex items-center gap-2 text-sm font-semibold text-lms-action"
          >
            View shipments
            <ArrowRight className="h-4 w-4" />
          </button>
        </div>

        <div className="rounded-xl border border-slate-200 p-5">
          <p className="text-sm font-medium text-lms-textMuted">
            Most Common Route
          </p>

          <p className="mt-2 break-words text-xl font-bold text-lms-navy">
            {loading ? "—" : mostCommonRoute}
          </p>

          <p className="mt-2 text-sm text-lms-textMuted">
            {loading
                ? "Calculating route statistics..."
                : `${mostCommonRouteCount} shipment${mostCommonRouteCount !== 1 ? "s" : ""}`}
          </p>
        </div>
      </div>
    </section>

    {/* TRACKING STATUS */}
    <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <div className="mb-6 flex flex-wrap items-start justify-between gap-4">
        <div className="flex items-start gap-3">
        <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-lms-ice text-lms-navy">
          <Activity className="h-5 w-5" />
        </span>

          <div>
            <h2 className="text-lg font-bold text-lms-textMain">
              Shipment Tracking
            </h2>

            <p className="mt-1 text-sm text-lms-textMuted">
              Current counts by tracking status.
            </p>
          </div>
        </div>

        <button
            type="button"
            onClick={() => setActiveTab("tracking")}
            className="inline-flex items-center gap-2 text-sm font-semibold text-lms-action"
        >
          Open tracking
          <ArrowRight className="h-4 w-4" />
        </button>
      </div>

      <div className="mb-5 rounded-xl border border-slate-200 bg-slate-50 p-5">
        <p className="text-sm font-medium text-lms-textMuted">
          Total Tracking Records
        </p>

        <p className="mt-2 text-3xl font-bold text-lms-navy">
          {loading ? "—" : shipmentStatusAnalytics.total}
        </p>
      </div>

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
        {statusCards.map((status) => (
            <div
                key={status.label}
                className="rounded-xl border border-slate-200 p-4"
            >
              <p className="text-sm font-medium text-lms-textMuted">
                {status.label}
              </p>

              <p className="mt-2 text-2xl font-bold text-lms-navy">
                {loading ? "—" : status.value}
              </p>
            </div>
        ))}
      </div>
    </section>

    {/* PROCESS OVERVIEW */}
    <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
      <h2 className="text-lg font-bold text-lms-textMain">
        How the System Works
      </h2>

      <p className="mt-1 text-sm text-lms-textMuted">
        The main modules supporting your logistics workflow.
      </p>

      <div className="mt-5 grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-6">
        {[
          "Company",
          "Inventory",
          "Contract",
          "Shipment",
          "Tracking",
          "Invoice"
        ].map((step, index) => (
            <div
                key={step}
                className="flex min-h-14 items-center justify-center gap-2 rounded-lg border border-slate-200 bg-slate-50 px-3 text-center text-sm font-semibold text-lms-navy"
            >
          <span className="text-xs text-slate-400">
            {String(index + 1).padStart(2, "0")}
          </span>
              {step}
            </div>
        ))}
      </div>
    </section>
  </div>


);
};

export default Home;
