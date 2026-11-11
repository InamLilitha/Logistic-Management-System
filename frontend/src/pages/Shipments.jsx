import React, { useCallback, useEffect, useMemo, useState } from "react";
import { RefreshCw } from "lucide-react";

import {
  getAllShipments,
  createShipment,
  updateShipment,
  deleteShipment
} from "../services/ShipmentService";

import { getAllContracts } from "../services/ContractService";
import { getAllCompanies } from "../services/CompanyService";
import { getAllInventory } from "../services/InventoryService";
import { useAuth } from "../auth/AuthContext";

import "./Shipments.css";

const EMPTY_FORM = {
  weight: "",
  origin: "",
  destination: "",
  dispatchDate: "",
  cargoType: "General Cargo",
  companyId: "",
  contractId: "",
  shipmentItems: [
    {
      inventoryId: "",
      quantityShipped: ""
    }
  ]
};

const CARGO_TYPES = [
  "General Cargo",
  "Fragile",
  "Perishable Goods",
  "Hazardous Materials"
];

function Shipments({ canEdit = true }) {
  const { currentUser } = useAuth();

  const [shipments, setShipments] = useState([]);
  const [shippingContracts, setShippingContracts] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [inventoryItems, setInventoryItems] = useState([]);

  const [formData, setFormData] = useState({ ...EMPTY_FORM });
  const [editingShipmentId, setEditingShipmentId] = useState(null);

  const [searchTerm, setSearchTerm] = useState("");
  const [cargoFilter, setCargoFilter] = useState("all");

  const [loading, setLoading] = useState(false);
  const [loadingOptions, setLoadingOptions] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // --------------------------------------------------
  // LOAD SHIPMENTS
  // --------------------------------------------------

  const loadShipments = useCallback(async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getAllShipments();

      console.log("RAW RESPONSE:", response);
      console.log("RAW DATA:", response.data);
      console.log("DATA TYPE:", typeof response.data);
      console.log("CONTENT TYPE:", response.headers["content-type"]);

      let data = response.data;

      if (typeof data === "string") {
        data = JSON.parse(data);
      }

      console.log("PROCESSED DATA:", data);
      console.log("IS ARRAY:", Array.isArray(data));

      if (!Array.isArray(data)) {
        console.error("Expected an array but received:", data);
        setShipments([]);
        setError("Shipment response is not an array. Check the console.");
        return;
      }

      setShipments(data);

      console.log("SUCCESS! Shipments loaded:", data.length);

    } catch (error) {
      console.error("SHIPMENT LOADING ERROR:", error);

      setShipments([]);
      setError("Could not process shipment data.");
    } finally {
      setLoading(false);
    }
  }, []);

  // --------------------------------------------------
  // LOAD FORM OPTIONS
  // --------------------------------------------------

  const loadFormOptions = useCallback(async () => {
    try {
      setLoadingOptions(true);

      const [
        contractsResponse,
        companiesResponse,
        inventoryResponse
      ] = await Promise.all([
        getAllContracts(),
        getAllCompanies(),
        getAllInventory()
      ]);

      setShippingContracts(
          Array.isArray(contractsResponse.data)
              ? contractsResponse.data
              : []
      );

      setCompanies(
          Array.isArray(companiesResponse.data)
              ? companiesResponse.data.filter(company => company.isActive !== false)
              : []
      );

      setInventoryItems(
          Array.isArray(inventoryResponse.data)
              ? inventoryResponse.data
              : []
      );

    } catch (err) {
      console.error("Could not load shipment form options:", err);

      setError(
          "Could not load companies, inventory or contracts. Please check the relevant endpoints."
      );
    } finally {
      setLoadingOptions(false);
    }
  }, []);

  useEffect(() => {
    loadShipments();
    loadFormOptions();
  }, [loadShipments, loadFormOptions]);

  // --------------------------------------------------
  // COMPANY INVENTORY
  // --------------------------------------------------

  const selectedCompany = companies.find(
      company => company.companyId === formData.companyId
  );

  const availableInventory = useMemo(() => {
    if (!formData.companyId || !selectedCompany) {
      return [];
    }

    return inventoryItems.filter(item => {
      const inventoryCompany = String(item.companyId || "");

      return (
          inventoryCompany === selectedCompany.companyId ||
          inventoryCompany === selectedCompany.companyName
      );
    });
  }, [inventoryItems, formData.companyId, selectedCompany]);

  // --------------------------------------------------
  // FORM HANDLING
  // --------------------------------------------------

  const loadInventory = async () => {
    try {
      const response = await getAllInventory();
      setInventory(Array.isArray(response.data) ? response.data : []);
    } catch (loadError) {
      setError("Inventory items could not be loaded. Shipment stock deductions are unavailable until the server connection is restored.");
    }
  };

  const addShipmentItem = () => {
    setShipmentItems(current => [...current, { inventoryId: "", quantity: "1" }]);
  };

  const updateShipmentItem = (index, field, value) => {
    setShipmentItems(current => current.map((item, itemIndex) =>
      itemIndex === index ? { ...item, [field]: value } : item
    ));
  };

  const removeShipmentItem = (index) => {
    setShipmentItems(current => current.filter((_, itemIndex) => itemIndex !== index));
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData(previous => ({
      ...previous,
      [name]: value
    }));
  };

  const handleCompanyChange = (event) => {
    const companyId = event.target.value;

    setFormData(previous => ({
      ...previous,
      companyId,
      shipmentItems: [
        {
          inventoryId: "",
          quantityShipped: ""
        }
      ]
    }));
  };

  const handleInventoryChange = (index, field, value) => {
    setFormData(previous => ({
      ...previous,
      shipmentItems: previous.shipmentItems.map((item, itemIndex) =>
          itemIndex === index
              ? {
                ...item,
                [field]: value
              }
              : item
      )
    }));
  };

  const addInventoryItem = () => {
    setFormData(previous => ({
      ...previous,
      shipmentItems: [
        ...previous.shipmentItems,
        {
          inventoryId: "",
          quantityShipped: ""
        }
      ]
    }));
  };

  const removeInventoryItem = (index) => {
    setFormData(previous => ({
      ...previous,
      shipmentItems: previous.shipmentItems.filter(
          (_, itemIndex) => itemIndex !== index
      )
    }));
  };

  const resetForm = () => {
    setFormData({
      ...EMPTY_FORM,
      shipmentItems: [
        {
          inventoryId: "",
          quantityShipped: ""
        }
      ]
    });

    setEditingShipmentId(null);
  };

  const handleCancelEdit = () => {
    resetForm();
    setError("");
    setSuccess("");
  };

  // --------------------------------------------------
  // CREATE OR UPDATE SHIPMENT
  // --------------------------------------------------

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    const weight = Number(formData.weight);

    if (!Number.isFinite(weight) || weight <= 0) {
      setError("Please enter a weight greater than zero.");
      return;
    }

    if (!formData.origin.trim() || !formData.destination.trim()) {
      setError("Please enter both an origin and a destination.");
      return;
    }

    if (
        formData.origin.trim().toLowerCase() ===
        formData.destination.trim().toLowerCase()
    ) {
      setError("Origin and destination must be different.");
      return;
    }

    if (!formData.dispatchDate) {
      setError("Please select a dispatch date.");
      return;
    }

    if (!formData.companyId) {
      setError("Please select a company.");
      return;
    }

    if (!formData.shipmentItems.length) {
      setError("Please add at least one inventory item.");
      return;
    }

    for (const item of formData.shipmentItems) {
      if (!item.inventoryId) {
        setError("Please select an inventory item for every allocation.");
        return;
      }

      const quantity = Number(item.quantityShipped);

      if (!Number.isInteger(quantity) || quantity <= 0) {
        setError("Each inventory quantity must be a positive whole number.");
        return;
      }
    }

    const inventoryIds = formData.shipmentItems.map(
        item => item.inventoryId
    );

    if (new Set(inventoryIds).size !== inventoryIds.length) {
      setError("Please do not select the same inventory item more than once.");
      return;
    }

    const wasEditing = Boolean(editingShipmentId);

    if (!wasEditing && !currentUser?.id) {
      setError(
          "Unable to identify the logged-in staff member. Please log in again."
      );
      return;
    }

    const shipment = {
      weight,
      origin: formData.origin.trim(),
      destination: formData.destination.trim(),
      dispatchDate: formData.dispatchDate,
      cargoType: formData.cargoType,

      company: {
        companyId: formData.companyId
      },

      contract: formData.contractId
          ? {
            contractId: formData.contractId
          }
          : null,

      shipmentItems: formData.shipmentItems.map(item => ({
        inventory: {
          inventoryId: item.inventoryId
        },
        quantityShipped: Number(item.quantityShipped)
      }))
    };

    try {
      setSubmitting(true);

      if (wasEditing) {
        await updateShipment({
          ...shipment,
          shipmentId: editingShipmentId
        });
      } else {
        await createShipment({
          ...shipment,
          createdByStaff: {
            id: currentUser.id
          }
        });
      }

      resetForm();

      setSuccess(
          wasEditing
              ? "Shipment updated successfully."
              : "Shipment created successfully."
      );

      await Promise.all([
        loadShipments(),
        loadFormOptions()
      ]);

    } catch (err) {
      console.error("Shipment submission failed:", err);

      const backendMessage =
          err.response?.data?.message ||
          (typeof err.response?.data === "string"
              ? err.response.data
              : "");

      setError(
          backendMessage ||
          (wasEditing
              ? "Could not update shipment. Please try again."
              : "Could not create shipment. Please try again.")
      );

    } finally {
      setSubmitting(false);
    }
  };

  // --------------------------------------------------
  // EDIT SHIPMENT
  // --------------------------------------------------

  const handleEdit = (shipment) => {
    const existingItems = (shipment.shipmentItems || []).map(item => ({
      inventoryId: item.inventory?.inventoryId || "",
      quantityShipped: item.quantityShipped ?? ""
    }));

    setEditingShipmentId(shipment.shipmentId);

    setFormData({
      weight: shipment.weight ?? "",
      origin: shipment.origin ?? "",
      destination: shipment.destination ?? "",
      dispatchDate: shipment.dispatchDate ?? "",
      cargoType: shipment.cargoType || "General Cargo",

      companyId: shipment.company?.companyId || "",

      contractId: shipment.contract?.contractId || "",

      shipmentItems: existingItems.length
          ? existingItems
          : [
            {
              inventoryId: "",
              quantityShipped: ""
            }
          ]
    });
    setShipmentItems([]);

    setError("");
    setSuccess("");

    window.scrollTo({
      top: 0,
      behavior: "smooth"
    });
  };

  // --------------------------------------------------
  // DELETE SHIPMENT
  // --------------------------------------------------

  const handleDelete = async (shipmentId) => {
    const confirmed = window.confirm(
        "Are you sure you want to delete this shipment?"
    );

    if (!confirmed) return;

    setError("");
    setSuccess("");

    try {
      await deleteShipment(shipmentId);

      setShipments(previous =>
          previous.filter(
              shipment => shipment.shipmentId !== shipmentId
          )
      );

      if (editingShipmentId === shipmentId) {
        resetForm();
      }

      setSuccess("Shipment deleted successfully.");

      await loadFormOptions();

    } catch (err) {
      console.error("Shipment deletion failed:", err);

      setError(
          err.response?.data?.message ||
          "Unable to delete shipment. Please try again."
      );
    }
  };

  // --------------------------------------------------
  // INVENTORY DISPLAY
  // --------------------------------------------------

  const getInventoryDetails = (shipment) => {
    const items = shipment.shipmentItems || [];

    if (!items.length) {
      return "Not assigned";
    }

    return items.map((shipmentItem, index) => {
      const inventory = shipmentItem.inventory;

      const itemName =
          inventory?.itemName ||
          inventory?.sku ||
          inventory?.inventoryId ||
          "Unknown item";

      const quantity = shipmentItem.quantityShipped;

      return (
          <div key={shipmentItem.shipmentItemId || index}>
            {itemName}
            {quantity != null && ` (Qty: ${quantity})`}
          </div>
      );
    });
  };

  // --------------------------------------------------
  // DISPLAY COMPANY
  // --------------------------------------------------

  const getCompanyName = (shipment) => {
    return (
        shipment.company?.companyName ||
        shipment.company?.companyId ||
        "Not assigned"
    );
  };

  // --------------------------------------------------
  // SEARCH AND FILTER
  // --------------------------------------------------

  const filteredShipments = useMemo(() => {
    const search = searchTerm.trim().toLowerCase();

    return shipments.filter(shipment => {
      const inventoryText = (shipment.shipmentItems || [])
          .map(item => {
            const inventory = item.inventory;

            return [
              inventory?.itemName,
              inventory?.sku,
              inventory?.inventoryId
            ].join(" ");
          })
          .join(" ");

      const matchesSearch = [
        shipment.shipmentId,
        shipment.origin,
        shipment.destination,
        shipment.weight,
        shipment.cargoType,
        shipment.company?.companyName,
        shipment.company?.companyId,
        shipment.contract?.contractId,
        shipment.contract?.contractNumber,
        inventoryText
      ].some(value =>
          String(value ?? "").toLowerCase().includes(search)
      );

      const matchesCargo =
          cargoFilter === "all" ||
          shipment.cargoType === cargoFilter;

      return matchesSearch && matchesCargo;
    });
  }, [shipments, searchTerm, cargoFilter]);

  // --------------------------------------------------
  // DASHBOARD STATISTICS
  // --------------------------------------------------

  const totalWeight = useMemo(
      () =>
          shipments.reduce((total, shipment) => {
            const weight = Number(shipment.weight);

            return total + (
                Number.isFinite(weight) ? weight : 0
            );
          }, 0),
      [shipments]
  );

  const uniqueCargoTypes = useMemo(
      () =>
          new Set(
              shipments
                  .map(shipment => shipment.cargoType)
                  .filter(Boolean)
          ).size,
      [shipments]
  );

  // --------------------------------------------------
  // DISPLAY HELPERS
  // --------------------------------------------------

  const formatDate = (date) => {
    if (!date) return "—";

    const parsedDate = new Date(`${date}T00:00:00`);

    if (Number.isNaN(parsedDate.getTime())) {
      return date;
    }

    return parsedDate.toLocaleDateString("en-ZA", {
      day: "2-digit",
      month: "short",
      year: "numeric"
    });
  };

  const getContractName = (shipment) => {
    const contract = shipment.contract;

    if (!contract) return "Not assigned";

    return (
        contract.contractNumber ||
        contract.contractId ||
        "Not assigned"
    );
  };

  // --------------------------------------------------
  // PAGE
  // --------------------------------------------------

  return (
      <div className="shipments-page">

        <header className="shipments-header">
          <div>
            <h1>Shipments</h1>
            <p>
              Manage cargo movements, inventory allocations,
              and shipping contracts.
            </p>
          </div>
        </header>

        {/* DASHBOARD STATISTICS */}

        <section
            className="shipment-overview"
            aria-label="Shipment overview"
        >
          <article className="shipment-stat-card">
            <div>
              <p className="shipment-stat-label">
                Total Shipments
              </p>

              <p className="shipment-stat-value">
                {shipments.length}
              </p>
            </div>
          </article>

          <article className="shipment-stat-card">
            <div>
              <p className="shipment-stat-label">
                Combined Cargo Weight
              </p>

              <p className="shipment-stat-value">
                {totalWeight.toLocaleString("en-ZA", {
                  maximumFractionDigits: 2
                })}{" "}

                <span style={{
                  fontSize: "13px",
                  fontWeight: 600
                }}>
                kg
              </span>
              </p>
            </div>
          </article>

          <article className="shipment-stat-card">
            <div>
              <p className="shipment-stat-label">
                Cargo Types Used
              </p>

              <p className="shipment-stat-value">
                {uniqueCargoTypes}
              </p>
            </div>
          </article>
        </section>

        {/* ALERTS */}

        {error && (
            <div
                className="shipment-message shipment-error"
                role="alert"
            >
              {error}
            </div>
        )}

        {success && (
            <div
                className="shipment-message shipment-success"
                role="status"
            >
              {success}
            </div>
        )}

        {/* CREATE / EDIT FORM */}

        {canEdit && (
            <section className="shipment-form-card">

              <div className="section-heading">
                <h2>
                  {editingShipmentId
                      ? "Edit Shipment"
                      : "Create a Shipment"}
                </h2>

                <p>
                  Select a company, allocate inventory,
                  and enter the shipment details.
                </p>
              </div>

              <form onSubmit={handleSubmit}>

                <div className="shipment-form-grid">

                  <div className="form-group">
                    <label htmlFor="companyId">
                      Company
                    </label>

                    <select
                        id="companyId"
                        name="companyId"
                        value={formData.companyId}
                        onChange={handleCompanyChange}
                        disabled={loadingOptions}
                        required
                    >
                      <option value="">
                        {loadingOptions
                            ? "Loading companies..."
                            : "Select a company"}
                      </option>

                      {companies.map(company => (
                          <option
                              key={company.companyId}
                              value={company.companyId}
                          >
                            {company.companyName}
                          </option>
                      ))}
                    </select>
                  </div>

                  <div className="form-group">
                    <label htmlFor="weight">
                      Weight (kg)
                    </label>

                    <input
                        id="weight"
                        name="weight"
                        type="number"
                        min="0.01"
                        step="0.01"
                        value={formData.weight}
                        onChange={handleChange}
                        placeholder="e.g. 250.50"
                        required
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="cargoType">
                      Cargo Type
                    </label>

                    <select
                        id="cargoType"
                        name="cargoType"
                        value={formData.cargoType}
                        onChange={handleChange}
                        required
                    >
                      {CARGO_TYPES.map(type => (
                          <option key={type} value={type}>
                            {type}
                          </option>
                      ))}
                    </select>
                  </div>

                  <div className="form-group">
                    <label htmlFor="origin">
                      Origin
                    </label>

                    <input
                        id="origin"
                        name="origin"
                        type="text"
                        value={formData.origin}
                        onChange={handleChange}
                        placeholder="Enter collection location"
                        required
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="destination">
                      Destination
                    </label>

                    <input
                        id="destination"
                        name="destination"
                        type="text"
                        value={formData.destination}
                        onChange={handleChange}
                        placeholder="Enter delivery location"
                        required
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="dispatchDate">
                      Dispatch Date
                    </label>

                    <input
                        id="dispatchDate"
                        name="dispatchDate"
                        type="date"
                        value={formData.dispatchDate}
                        onChange={handleChange}
                        required
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="contractId">
                      Shipping Contract
                    </label>

                    <select
                        id="contractId"
                        name="contractId"
                        value={formData.contractId}
                        onChange={handleChange}
                        disabled={loadingOptions}
                    >
                      <option value="">
                        No contract selected
                      </option>

                      {shippingContracts.map(contract => (
                          <option
                              key={contract.contractId}
                              value={contract.contractId}
                          >
                            {contract.contractNumber ||
                                contract.contractId}
                            {" "}— {contract.pricePerKg ?? "N/A"} per kg
                          </option>
                      ))}
                    </select>
                  </div>

                </div>

                {/* INVENTORY ALLOCATION */}

                <div className="section-heading" style={{
                  marginTop: "28px"
                }}>
                  <h2>Inventory Allocation</h2>

                  <p>
                    Select the inventory items and quantities
                    included in this shipment.
                  </p>
                </div>

                {formData.shipmentItems.map((item, index) => (
                    <div
                        className="shipment-form-grid"
                        key={index}
                        style={{
                          marginBottom: "18px",
                          alignItems: "end"
                        }}
                    >

                      <div className="form-group">
                        <label htmlFor={`inventory-${index}`}>
                          Inventory Item {index + 1}
                        </label>

                        <select
                            id={`inventory-${index}`}
                            value={item.inventoryId}
                            onChange={event =>
                                handleInventoryChange(
                                    index,
                                    "inventoryId",
                                    event.target.value
                                )
                            }
                            disabled={
                                loadingOptions ||
                                !formData.companyId
                            }
                            required
                        >
                          <option value="">
                            {!formData.companyId
                                ? "Select a company first"
                                : availableInventory.length === 0
                                    ? "No inventory available"
                                    : "Select inventory"}
                          </option>

                          {availableInventory
                              .filter(inventory =>
                                  inventory.inventoryId === item.inventoryId ||
                                  !formData.shipmentItems.some(
                                      (other, otherIndex) =>
                                          otherIndex !== index &&
                                          other.inventoryId === inventory.inventoryId
                                  )
                              )
                              .map(inventory => (
                                  <option
                                      key={inventory.inventoryId}
                                      value={inventory.inventoryId}
                                  >
                                    {inventory.itemName}
                                    {" "}— Stock: {inventory.quantityAvailable}
                                  </option>
                              ))}
                        </select>
                      </div>

                      <div className="form-group">
                        <label htmlFor={`quantity-${index}`}>
                          Quantity Shipped
                        </label>

                        <input
                            id={`quantity-${index}`}
                            type="number"
                            min="1"
                            step="1"
                            value={item.quantityShipped}
                            onChange={event =>
                                handleInventoryChange(
                                    index,
                                    "quantityShipped",
                                    event.target.value
                                )
                            }
                            placeholder="Enter quantity"
                            required
                        />
                      </div>

                      {formData.shipmentItems.length > 1 && (
                          <div className="form-group">
                            <button
                                type="button"
                                className="delete-button"
                                onClick={() =>
                                    removeInventoryItem(index)
                                }
                            >
                              Remove Item
                            </button>
                          </div>
                      )}

                    </div>
                ))}

                <button
                    type="button"
                    className="secondary-button"
                    onClick={addInventoryItem}
                    disabled={
                        !formData.companyId ||
                        availableInventory.length <= formData.shipmentItems.length
                    }
                >
                  + Add Another Inventory Item
                </button>

                {/* FORM ACTIONS */}

                <div className="form-actions">

                  {editingShipmentId && (
                      <button
                          type="button"
                          className="secondary-button"
                          onClick={handleCancelEdit}
                          disabled={submitting}
                      >
                        Cancel
                      </button>
                  )}

                  <button
                      type="submit"
                      className="primary-button"
                      disabled={submitting || loadingOptions}
                  >
                    {submitting
                        ? "Saving..."
                        : editingShipmentId
                            ? "Save Changes"
                            : "Create Shipment"}
                  </button>

                </div>

              </form>
            </section>
        )}

        {/* SHIPMENT RECORDS */}

        <section className="shipment-list-section">

          <div className="shipment-list-heading">

            <div className="shipment-list-title">
              <h2>Shipment Records</h2>

              <p>
                Search and review registered shipments.
              </p>
            </div>

            <div className="shipment-list-tools">

              <input
                  type="search"
                  aria-label="Search shipments"
                  placeholder="Search ID, company, origin..."
                  value={searchTerm}
                  onChange={event =>
                      setSearchTerm(event.target.value)
                  }
                  className="shipment-search"
              />

              <select
                  aria-label="Filter by cargo type"
                  value={cargoFilter}
                  onChange={event =>
                      setCargoFilter(event.target.value)
                  }
                  className="shipment-filter"
              >
                <option value="all">
                  All cargo types
                </option>

                {CARGO_TYPES.map(type => (
                    <option key={type} value={type}>
                      {type}
                    </option>
                ))}
              </select>

              {/* REFRESH ICON */}

              <button
                  type="button"
                  className="refresh-shipments-btn"
                  onClick={loadShipments}
                  disabled={loading}
                  aria-label="Refresh shipments"
                  title="Refresh shipments"
              >
                <RefreshCw
                    size={16}
                    className={loading ? "refresh-spinning" : ""}
                />
              </button>

            </div>
          </div>

          {loading ? (

              <div className="shipment-empty-state">
                <strong>Loading shipments...</strong>
                <p>
                  Please wait while shipment records are retrieved.
                </p>
              </div>

          ) : shipments.length === 0 ? (

              <div className="shipment-empty-state">
                <strong>No shipments yet</strong>
                <p>
                  Once a shipment is created, it will appear here.
                </p>
              </div>

          ) : filteredShipments.length === 0 ? (

              <div className="shipment-empty-state">
                <strong>No matching shipments</strong>
                <p>
                  Try a different search term or cargo type.
                </p>
              </div>

          ) : (

              <>
                <div className="shipment-table-container">

                  <table className="shipment-table">

                    <thead>
                    <tr>
                      <th>Shipment ID</th>
                      <th>Company</th>
                      <th>Weight</th>
                      <th>Cargo Type</th>
                      <th>Origin</th>
                      <th>Destination</th>
                      <th>Dispatch Date</th>
                      <th>Estimated Delivery</th>
                      <th>Inventory</th>
                      <th>Contract</th>

                      {canEdit && <th>Actions</th>}
                    </tr>
                    </thead>

                    <tbody>
                    {filteredShipments.map(shipment => (

                        <tr key={shipment.shipmentId}>

                          <td className="shipment-id">
                            {shipment.shipmentId || "—"}
                          </td>

                          <td>
                            {getCompanyName(shipment)}
                          </td>

                          <td className="shipment-weight">
                            {shipment.weight ?? "—"} kg
                          </td>

                          <td>
                        <span className="cargo-type-text">
                          {shipment.cargoType || "Unspecified"}
                        </span>
                          </td>

                          <td className="shipment-location">
                            {shipment.origin || "—"}
                          </td>

                          <td className="shipment-location">
                            {shipment.destination || "—"}
                          </td>

                          <td className="shipment-date">
                            {formatDate(shipment.dispatchDate)}
                          </td>

                          <td className="shipment-date">
                            {formatDate(shipment.estimatedDeliveryDate)}
                          </td>

                          <td>
                            {getInventoryDetails(shipment)}
                          </td>

                          <td>
                            {getContractName(shipment)}
                          </td>

                          {canEdit && (
                              <td>
                                <div className="shipment-actions">

                                  <button
                                      type="button"
                                      className="edit-button"
                                      onClick={() =>
                                          handleEdit(shipment)
                                      }
                                  >
                                    Edit
                                  </button>

                                  <button
                                      type="button"
                                      className="delete-button"
                                      onClick={() =>
                                          handleDelete(shipment.shipmentId)
                                      }
                                  >
                                    Delete
                                  </button>

                                </div>
                              </td>
                          )}

                        </tr>
                    ))}
                    </tbody>

                  </table>

                </div>

                <p className="shipment-results-count">
                  Showing {filteredShipments.length} of{" "}
                  {shipments.length} shipments
                </p>
              </>
          )}
        </section>

      </div>
  );
}

export default Shipments;