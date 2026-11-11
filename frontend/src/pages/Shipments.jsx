import React, { useEffect, useState } from "react";
import {
  getAllShipments,
  createShipment,
  updateShipment,
  deleteShipment
} from "../services/ShipmentService";
import { getAllInventory } from "../services/InventoryService.js";
import "./Shipments.css";

function Shipments() {

  const [shipments, setShipments] = useState([]);
  const [inventory, setInventory] = useState([]);
  const [shipmentItems, setShipmentItems] = useState([]);

  const [formData, setFormData] = useState({
    weight: "",
    origin: "",
    destination: "",
    dispatchDate: "",
    cargoType: "General Cargo"
  });
  const [editingShipmentId, setEditingShipmentId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    loadShipments();
    loadInventory();
  }, []);

  const loadShipments = async () => {
    try {
      setLoading(true);

      const response = await getAllShipments();
      setShipments(response.data);

    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

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

    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    try {
      const shipment = {
        weight: Number(formData.weight),
        origin: formData.origin.trim(),
        destination: formData.destination.trim(),
        dispatchDate: formData.dispatchDate,
        cargoType: formData.cargoType,
        items: editingShipmentId ? [] : shipmentItems.map(item => ({
          inventoryId: item.inventoryId,
          quantity: Number(item.quantity)
        }))
      };

      if (editingShipmentId) {

        await updateShipment({
          ...shipment,
          shipmentId: editingShipmentId
        });

        setSuccess("Shipment updated successfully.");

      } else {

        await createShipment(shipment);

        setSuccess("Shipment created successfully.");
      }

      setFormData({
        weight: "",
        origin: "",
        destination: "",
        dispatchDate: "",
        cargoType: "General Cargo"
      });
      setShipmentItems([]);

      setEditingShipmentId(null);

      await loadShipments();
      if (!editingShipmentId) await loadInventory();

    } catch (error) {

      console.error("Shipment submission failed:", error);

      setError(
          editingShipmentId
              ? (error.response?.data?.detail || error.response?.data?.message || "Could not update shipment.")
              : (error.response?.data?.detail || error.response?.data?.message || "Could not create shipment.")
      );
    }
  };
  const filteredShipments = shipments.filter((shipment) => {
    const search = searchTerm.toLowerCase().trim();

    return (
        shipment.shipmentId?.toLowerCase().includes(search) ||
        shipment.origin?.toLowerCase().includes(search) ||
        shipment.destination?.toLowerCase().includes(search) ||
        shipment.weight?.toString().includes(search) ||
        shipment.cargoType?.toLowerCase().includes(search)
    );
  });
  const handleEdit = (shipment) => {

    setEditingShipmentId(shipment.shipmentId);

    setFormData({
      weight: shipment.weight,
      origin: shipment.origin,
      destination: shipment.destination,
      dispatchDate: shipment.dispatchDate,
      cargoType: shipment.cargoType || "General Cargo"
    });
    setShipmentItems([]);

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

    if (!confirmed) {
      return;
    }

    try {
      setError("");
      setSuccess("");

      await deleteShipment(shipmentId);

      setSuccess("Shipment deleted successfully.");

      loadShipments();

    } catch (error) {
      console.error(error);
      setError("Unable to delete shipment.");
    }
  };

  return (
      <div className="shipments-page">

        <div className="shipments-header">
          <div>
            <h1>Shipments</h1>
            <p>Manage and track shipments across the logistics system.</p>
          </div>
        </div>

        <section className="shipment-form-card">

          <div className="section-heading">
            <h2>{editingShipmentId ? "Edit Shipment" : "Create Shipment"}</h2>
            <p>{editingShipmentId ? "Update the shipment details below." : "Enter the shipment details below."}</p>
          </div>

          {error && (
              <div className="shipment-message shipment-error">
                {error}
              </div>
          )}

          {success && (
              <div className="shipment-message shipment-success">
                {success}
              </div>
          )}

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
                    placeholder="Enter weight"
                    required
                />
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
                    placeholder="Enter origin"
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
                    placeholder="Enter destination"
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
                  <option value="General Cargo">General Cargo</option>
                  <option value="Fragile">Fragile</option>
                  <option value="Perishable Goods">Perishable Goods</option>
                  <option value="Hazardous Materials">Hazardous Materials</option>
                </select>
              </div>

            </div>

            {!editingShipmentId && (
              <section className="shipment-items-section" aria-labelledby="shipment-items-heading">
                <div className="shipment-items-heading">
                  <div>
                    <h3 id="shipment-items-heading">Inventory items</h3>
                    <p>Add stock to deduct when this shipment is created. This is optional.</p>
                  </div>
                  <button type="button" className="secondary-button" onClick={addShipmentItem}>
                    Add item
                  </button>
                </div>
                {shipmentItems.map((item, index) => {
                  const selectedInventory = inventory.find(record => record.inventoryId === item.inventoryId);
                  return (
                    <div className="shipment-item-row" key={`shipment-item-${index}`}>
                      <label>
                        SKU / item
                        <select
                          value={item.inventoryId}
                          onChange={event => updateShipmentItem(index, "inventoryId", event.target.value)}
                          required
                        >
                          <option value="">Select a SKU</option>
                          {inventory.map(record => (
                            <option key={record.inventoryId} value={record.inventoryId}>
                              {record.sku} - {record.itemName} ({record.quantityAvailable} available)
                            </option>
                          ))}
                        </select>
                      </label>
                      <label>
                        Quantity
                        <input
                          type="number"
                          min="1"
                          max={selectedInventory?.quantityAvailable}
                          step="1"
                          value={item.quantity}
                          onChange={event => updateShipmentItem(index, "quantity", event.target.value)}
                          required
                        />
                      </label>
                      <button
                        type="button"
                        className="secondary-button shipment-item-remove"
                        onClick={() => removeShipmentItem(index)}
                        aria-label={`Remove item ${index + 1}`}
                      >
                        Remove
                      </button>
                    </div>
                  );
                })}
                {inventory.length === 0 && (
                  <p className="shipment-item-hint">No inventory records are available to add.</p>
                )}
              </section>
            )}

            <div className="form-actions">

              <button
                  type="submit"
                  className="primary-button"
              >
                {editingShipmentId ? "Save Changes" : "Create Shipment"}
              </button>

              {editingShipmentId && (
                  <button
                      type="button"
                      className="secondary-button"
                      onClick={() => {
                        setEditingShipmentId(null);

                        setFormData({
                          weight: "",
                          origin: "",
                          destination: "",
                          dispatchDate: "",
                          cargoType: "General Cargo"
                        });
                        setShipmentItems([]);

                        setError("");
                        setSuccess("");
                      }}
                  >
                    Cancel
                  </button>
              )}

            </div>

          </form>

        </section>


        <section className="shipment-list-section">

          <div className="mb-5 flex items-center justify-between gap-4">
            <h2 className="text-lg font-bold text-lms-textMain">
              Shipment Records
            </h2>

            <input
                type="text"
                placeholder="Search shipments..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-56 rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-lms-action focus:ring-1 focus:ring-lms-action"
            />
          </div>


          {loading ? (

              <div className="shipment-empty-state">
                Loading shipments...
              </div>

          ) : shipments.length === 0 ? (

              <div className="shipment-empty-state">
                No shipments have been created yet.
              </div>



          ) : (


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
                    <th>Stock items</th>
                    <th>Actions</th>
                  </tr>
                  </thead>


                  <tbody>

                  {filteredShipments.map((shipment) => (

                      <tr key={shipment.shipmentId}>

                        <td className="shipment-id">
                          {shipment.shipmentId}
                        </td>

                        <td>
                          {shipment.weight} kg
                        </td>

                        <td>{shipment.cargoType}</td>


                        <td>
                          {shipment.origin}
                        </td>

                        <td>
                          {shipment.destination}
                        </td>


                        <td>
                          {shipment.dispatchDate}
                        </td>

                        <td>
                          {shipment.estimatedDeliveryDate}
                        </td>

                        <td>
                          {shipment.items?.length
                            ? shipment.items.map(item => `${item.sku} x ${item.quantity}`).join(", ")
                            : "None"}
                        </td>

                        <td className="shipment-actions">

                          <button
                              type="button"
                              className="edit-button"
                              onClick={() => handleEdit(shipment)}
                          >
                            Edit
                          </button>

                          <button
                              type="button"
                              className="delete-button"
                              onClick={() => handleDelete(shipment.shipmentId)}
                          >
                            Delete
                          </button>

                        </td>

                      </tr>

                  ))}

                  </tbody>

                </table>

              </div>

          )}

        </section>

      </div>
  );
}

export default Shipments;