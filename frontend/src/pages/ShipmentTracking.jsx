import React, { useEffect, useState } from 'react';
import {
  getShipmentTracking,
  createShipmentTracking,
  updateShipmentTracking,
  deleteShipmentTracking
} from '../services/ShipmentTrackingService';

import './ShipmentTracking.css';

function ShipmentTracking({ canEdit = true }) {

  const [trackingRecords, setTrackingRecords] = useState([]);

  const [formData, setFormData] = useState({
    trackingId: '',
    trackingNumber: '',
    currentLocation: '',
    shipmentStatus: 'In Transit',
    dateCreated: new Date().toISOString().slice(0, 10)
  });

  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadTrackingRecords();
  }, []);

  async function loadTrackingRecords() {
    try {
      const data = await getShipmentTracking();
      setTrackingRecords(data);
    } catch (error) {
      console.error(
          'Failed to load shipment tracking records from the backend',
          error
      );
    }
  }

  function handleInputChange(event) {
    const { name, value } = event.target;

    setFormData(current => ({
      ...current,
      [name]: value
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    try {

      const trackingData = {
        trackingId: formData.trackingId,
        trackingNumber: formData.trackingNumber,
        currentLocation: formData.currentLocation,
        shipmentStatus: formData.shipmentStatus,
        dateCreated: formData.dateCreated
      };

      await createShipmentTracking(trackingData);

      setFormData({
        trackingId: '',
        trackingNumber: '',
        currentLocation: '',
        shipmentStatus: 'In Transit',
        dateCreated: new Date().toISOString().slice(0, 10)
      });

      loadTrackingRecords();

    } catch (error) {
      console.error(
          'Failed to create shipment tracking record',
          error
      );
    }
  }

  async function handleDelete(trackingId) {
    try {
      await deleteShipmentTracking(trackingId);
      loadTrackingRecords();
    } catch (error) {
      console.error(
          'Failed to delete shipment tracking record',
          error
      );
    }
  }

  const filteredRecords = trackingRecords.filter(record =>
      record.trackingId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      record.trackingNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      record.currentLocation?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      record.shipmentStatus?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (

      <div className="shipment-tracking-container">

        <header className="page-header">
          <h1>Shipment Tracking</h1>
          <p>Monitor and track shipments across the logistics system.</p>
        </header>


        {/* Create Tracking Record */}

        {canEdit && <section className="tracking-form-card">

          <h2>Create Tracking Record</h2>

          <p className="form-description">
            Enter the shipment tracking details below.
          </p>

          <form
              className="tracking-form"
              onSubmit={handleSubmit}
          >

            <label>
              Tracking ID
              <input
                  name="trackingId"
                  placeholder="TRK-001"
                  value={formData.trackingId}
                  onChange={handleInputChange}
                  required
              />
            </label>


            <label>
              Tracking Number
              <input
                  name="trackingNumber"
                  placeholder="TRACK-001"
                  value={formData.trackingNumber}
                  onChange={handleInputChange}
                  required
              />
            </label>


            <label>
              Current Location
              <input
                  name="currentLocation"
                  placeholder="Cape Town"
                  value={formData.currentLocation}
                  onChange={handleInputChange}
                  required
              />
            </label>


            <label>
              Shipment Status
              <select
                  name="shipmentStatus"
                  value={formData.shipmentStatus}
                  onChange={handleInputChange}
              >
                <option value="Pending">Pending</option>
                <option value="In Transit">In Transit</option>
                <option value="Out for Delivery">
                  Out for Delivery
                </option>
                <option value="Delivered">Delivered</option>
                <option value="Delayed">Delayed</option>
              </select>
            </label>


            <label>
              Date Created
              <input
                  name="dateCreated"
                  type="date"
                  value={formData.dateCreated}
                  onChange={handleInputChange}
                  required
              />
            </label>


            <button
                type="submit"
                className="save-tracking-btn"
            >
              Create Tracking
            </button>

          </form>

        </section>}


        {/* Tracking Records */}

        <section className="tracking-list-container">

          <div className="tracking-list-header">

            <div>
              <h2>Shipment Tracking Records</h2>

              <p>
                View the shipments currently being tracked
                in the system.
              </p>
            </div>

            <input
                className="tracking-search-input"
                type="text"
                placeholder="Search tracking..."
                value={searchTerm}
                onChange={(event) =>
                    setSearchTerm(event.target.value)
                }
            />

          </div>


          <div className="tracking-table-wrapper">

            <table className="tracking-table">

              <thead>

              <tr>
                <th>Tracking ID</th>
                <th>Tracking Number</th>
                <th>Current Location</th>
                <th>Status</th>
                <th>Date Created</th>
                <th>Actions</th>
              </tr>

              </thead>


              <tbody>

              {filteredRecords.length === 0 ? (

                  <tr>
                    <td
                        colSpan="6"
                        className="empty-state"
                    >
                      No shipment tracking records found.
                    </td>
                  </tr>

              ) : (

                  filteredRecords.map(record => (

                      <tr key={record.trackingId}>

                        <td>
                          {record.trackingId}
                        </td>

                        <td>
                          {record.trackingNumber}
                        </td>

                        <td>
                          {record.currentLocation}
                        </td>

                        <td>

                                            <span
                                                className={`status-badge status-${record.shipmentStatus
                                                    ?.toLowerCase()
                                                    .replace(/\s+/g, '-')}`}
                                            >
                                                {record.shipmentStatus}
                                            </span>

                        </td>

                        <td>
                          {record.dateCreated
                              ? new Date(
                                  record.dateCreated
                              ).toLocaleDateString()
                              : ''}
                        </td>

                        <td>

                          {canEdit && <button
                              className="delete-tracking-btn"
                              onClick={() =>
                                  handleDelete(
                                      record.trackingId
                                  )
                              }
                          >
                            Delete
                          </button>}

                        </td>

                      </tr>

                  ))

              )}

              </tbody>

            </table>

          </div>

        </section>

      </div>
  );
}

export default ShipmentTracking;