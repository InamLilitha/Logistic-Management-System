
import React, { useCallback, useEffect, useMemo, useState } from "react";

import {
  getAllShipments,
  createShipment,
  updateShipment,
  deleteShipment
} from "../services/ShipmentService";

import { getAllInventory } from "../services/InventoryService";
import { getAllContracts } from "../services/ContractService";

import "./Shipments.css";

const EMPTY_FORM = {
  weight: "",
  origin: "",
  destination: "",
  dispatchDate: "",
  cargoType: "General Cargo",
  inventoryId: "",
  contractId: ""
};

const CARGO_TYPES = [
  "General Cargo",
  "Fragile",
  "Perishable Goods",
  "Hazardous Materials"
];

function Shipments({ canEdit = true }) {
  const [shipments, setShipments] = useState([]);
  const [inventoryItems, setInventoryItems] = useState([]);
  const [shippingContracts, setShippingContracts] = useState([]);

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

      setShipments(
        Array.isArray(response.data) ? response.data : []
      );
    } catch (err) {
      console.error("Could not load shipments:", err);

      setError(
        err.response?.data?.message ||
          "Could not load shipments. Please check your backend connection."
      );
    } finally {
      setLoading(false);
    }
  }, []);


  // LOAD INVENTORY AND CONTRACT OPTIONS


  const loadFormOptions = useCallback(async () => {
    try {
      setLoadingOptions(true);

      const [inventoryResponse, contractResponse] =
        await Promise.all([
          getAllInventory(),
          getAllContracts()
        ]);

      setInventoryItems(
        Array.isArray(inventoryResponse.data)
          ? inventoryResponse.data
          : []
      );

      setShippingContracts(
        Array.isArray(contractResponse.data)
          ? contractResponse.data
          : []
      );
    } catch (err) {
      console.error(
        "Could not load inventory or contracts:",
        err
      );

      setError(
        "Could not load inventory or contracts. Check that both backend endpoints are working."
      );
    } finally {
      setLoadingOptions(false);
    }
  }, []);

  useEffect(() => {
    loadShipments();
    loadFormOptions();
  }, [loadShipments, loadFormOptions]);


  // FORM HANDLING

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value
    }));
  };

  const resetForm = () => {
    setFormData({ ...EMPTY_FORM });
    setEditingShipmentId(null);
  };

  const handleCancelEdit = () => {
    resetForm();
    setError("");
    setSuccess("");
  };

  // CREATE OR UPDATE SHIPMENT
  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    const weight = Number(formData.weight);

    if (!Number.isFinite(weight) || weight <= 0) {
      setError("Please enter a weight greater than zero.");
      return;
    }

    if (
      !formData.origin.trim() ||
      !formData.destination.trim()
    ) {
      setError("Please enter both an origin and a destination.");
      return;
    }

    if (formData.origin.trim().toLowerCase() ===
        formData.destination.trim().toLowerCase()) {
      setError("Origin and destination must be different.");
      return;
    }

    if (!formData.dispatchDate) {
      setError("Please select a dispatch date.");
      return;
    }

    const shipment = {
      weight,
      origin: formData.origin.trim(),
      destination: formData.destination.trim(),
      dispatchDate: formData.dispatchDate,
      cargoType: formData.cargoType,
      inventoryId: formData.inventoryId || null,
      contractId: formData.contractId || null
    };

    const wasEditing = Boolean(editingShipmentId);

    try {
      setSubmitting(true);

      if (wasEditing) {
        await updateShipment({
          ...shipment,
          shipmentId: editingShipmentId
        });
      } else {
        await createShipment(shipment);
      }

      resetForm();

      setSuccess(
        wasEditing
          ? "Shipment updated successfully."
          : "Shipment created successfully."
      );

      await loadShipments();
    } catch (err) {
      console.error("Shipment submission failed:", err);

      const backendMessage =
        err.response?.data?.message ||
        (typeof err.response?.data === "string"
          ? err.response.data
          : "");

      setError(
        backendMessage ||
          (err.response?.status === 400
            ? "The shipment data or selected inventory/contract may be invalid."
            : wasEditing
              ? "Could not update shipment. Please try again."
              : "Could not create shipment. Please try again.")
      );
    } finally {
      setSubmitting(false);
    }
  };


  const handleEdit = (shipment) => {
    setEditingShipmentId(shipment.shipmentId);

    setFormData({
      weight: shipment.weight ?? "",
      origin: shipment.origin ?? "",
      destination: shipment.destination ?? "",
      dispatchDate: shipment.dispatchDate ?? "",
      cargoType: shipment.cargoType || "General Cargo",
      inventoryId: shipment.inventoryId ?? "",
      contractId: shipment.contractId ?? ""
    });

    setError("");
    setSuccess("");

    window.scrollTo({
      top: 0,
      behavior: "smooth"
    });
  };
  const handleDelete = async (shipmentId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this shipment?"
    );

    if (!confirmed) return;

    setError("");
    setSuccess("");

    try {
      await deleteShipment(shipmentId);

      setShipments((previous) =>
        previous.filter(
          (shipment) => shipment.shipmentId !== shipmentId
        )
      );

      if (editingShipmentId === shipmentId) {
        resetForm();
      }

      setSuccess("Shipment deleted successfully.");
    } catch (err) {
      console.error("Shipment deletion failed:", err);

      setError(
        err.response?.data?.message ||
          "Unable to delete shipment. Please try again."
      );
    }
  };

  const filteredShipments = useMemo(() => {
    const search = searchTerm.trim().toLowerCase();

    return shipments.filter((shipment) => {
      const matchesSearch = [
        shipment.shipmentId,
        shipment.origin,
        shipment.destination,
        shipment.weight,
        shipment.cargoType,
        shipment.inventoryId,
        shipment.contractId
      ].some((value) =>
        String(value ?? "").toLowerCase().includes(search)
      );

      const matchesCargo =
        cargoFilter === "all" ||
        shipment.cargoType === cargoFilter;

      return matchesSearch && matchesCargo;
    });
  }, [shipments, searchTerm, cargoFilter]);


  const totalWeight = useMemo(
    () =>
      shipments.reduce((total, shipment) => {
        const weight = Number(shipment.weight);

        return total +
          (Number.isFinite(weight) ? weight : 0);
      }, 0),
    [shipments]
  );

  const uniqueCargoTypes = useMemo(
    () =>
      new Set(
        shipments
          .map((shipment) => shipment.cargoType)
          .filter(Boolean)
      ).size,
    [shipments]
  );


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

  const getInventoryName = (inventoryId) => {
    if (!inventoryId) return "Not assigned";

    const item = inventoryItems.find(
      (inventory) => inventory.inventoryId === inventoryId
    );

    return item
      ? `${item.itemName} (${item.sku})`
      : inventoryId;
  };

  const getContractName = (contractId) => {
    if (!contractId) return "Not assigned";

    const contract = shippingContracts.find(
      (item) => item.contractId === contractId
    );

    return contract
      ? contract.contractNumber || contract.contractId
      : contractId;
  };


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
              <span style={{ fontSize: "13px", fontWeight: 600 }}>
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
              {editingShipmentId
                ? "Update the shipment details and associated records."
                : "Enter cargo details and optionally associate inventory and a shipping contract."}
            </p>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="shipment-form-grid">
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
                  {CARGO_TYPES.map((type) => (
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

              {/* INVENTORY DROPDOWN */}

              <div className="form-group">
                <label htmlFor="inventoryId">
                  Inventory Item
                </label>

                <select
                  id="inventoryId"
                  name="inventoryId"
                  value={formData.inventoryId}
                  onChange={handleChange}
                  disabled={loadingOptions}
                >
                  <option value="">
                    {loadingOptions
                      ? "Loading inventory..."
                      : "No inventory item selected"}
                  </option>

                  {inventoryItems.map((item) => (
                    <option
                      key={item.inventoryId}
                      value={item.inventoryId}
                    >
                      {item.itemName} — {item.sku} — Available:{" "}
                      {item.quantityAvailable}
                    </option>
                  ))}
                </select>

                {!loadingOptions &&
                  inventoryItems.length === 0 && (
                    <small>
                      No inventory records found. You can add
                      inventory from the Inventory page.
                    </small>
                  )}
              </div>

              {/* SHIPPING CONTRACT DROPDOWN */}

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
                    {loadingOptions
                      ? "Loading contracts..."
                      : "No contract selected"}
                  </option>

                  {shippingContracts.map((contract) => (
                    <option
                      key={contract.contractId}
                      value={contract.contractId}
                    >
                      {contract.contractNumber} —{" "}
                      {contract.pricePerKg} per kg
                    </option>
                  ))}
                </select>

                {!loadingOptions &&
                  shippingContracts.length === 0 && (
                    <small>
                      No shipping contracts found. You can add
                      a contract from the Contracts page.
                    </small>
                  )}
              </div>
            </div>

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
              placeholder="Search ID, origin, destination..."
              value={searchTerm}
              onChange={(event) =>
                setSearchTerm(event.target.value)
              }
              className="shipment-search"
            />

            <select
              aria-label="Filter by cargo type"
              value={cargoFilter}
              onChange={(event) =>
                setCargoFilter(event.target.value)
              }
              className="shipment-filter"
            >
              <option value="all">
                All cargo types
              </option>

              {CARGO_TYPES.map((type) => (
                <option key={type} value={type}>
                  {type}
                </option>
              ))}
            </select>
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
                  {filteredShipments.map((shipment) => (
                    <tr key={shipment.shipmentId}>
                      <td className="shipment-id">
                        {shipment.shipmentId || "—"}
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
                        {formatDate(
                          shipment.estimatedDeliveryDate
                        )}
                      </td>

                      <td>
                        {getInventoryName(shipment.inventoryId)}
                      </td>

                      <td>
                        {getContractName(shipment.contractId)}
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
                                handleDelete(
                                  shipment.shipmentId
                                )
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

