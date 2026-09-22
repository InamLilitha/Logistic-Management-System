import React, { useEffect, useState } from "react";
import {
  getAllShipments,
  createShipment,
  deleteShipment
} from "../services/ShipmentService";
import "./Shipments.css";

function Shipments() {

  const [shipments, setShipments] = useState([]);

  const [formData, setFormData] = useState({
    weight: "",
    origin: "",
    destination: "",
    dispatchDate: ""
  });

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
        origin: formData.origin,
        destination: formData.destination,
        dispatchDate: formData.dispatchDate
      };

      await createShipment(shipment);

      setSuccess("Shipment created successfully.");

      setFormData({
        weight: "",
        origin: "",
        destination: "",
        dispatchDate: ""
      });

      loadShipments();

    } catch (error) {
      console.error(error);
      setError("Could not create shipment.");
    }
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
            <h2>Create Shipment</h2>
            <p>Enter the shipment details below.</p>
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

            </div>

            <div className="form-actions">

              <button
                  type="submit"
                  className="primary-button"
              >
                Create Shipment
              </button>

            </div>

          </form>

        </section>

        <section className="shipment-list-section">

          <div className="section-heading">
            <h2>Shipment Records</h2>
            <p>View the shipments currently registered in the system.</p>
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
                    <th>Origin</th>
                    <th>Destination</th>
                    <th>Dispatch Date</th>
                    <th>Estimated Delivery</th>
                    <th>Action</th>
                  </tr>
                  </thead>

                  <tbody>

                  {shipments.map((shipment) => (

                      <tr key={shipment.shipmentId}>

                        <td className="shipment-id">
                          {shipment.shipmentId}
                        </td>

                        <td>
                          {shipment.weight} kg
                        </td>

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

                          <button
                              type="button"
                              className="delete-button"
                              onClick={() =>
                                  handleDelete(shipment.shipmentId)
                              }
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