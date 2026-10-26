import React, { useEffect, useState } from "react";
import {
  getAllShipments,
  createShipment,
  updateShipment,
  deleteShipment
} from "../services/ShipmentService";
import "./Shipments.css";

function Shipments() {

  const [shipments, setShipments] = useState([]);

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
        cargoType: formData.cargoType
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

      setEditingShipmentId(null);

      await loadShipments();

    } catch (error) {

      console.error("Shipment submission failed:", error);

      setError(
          editingShipmentId
              ? "Could not update shipment."
              : "Could not create shipment."
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