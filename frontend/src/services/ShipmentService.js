import axios from "axios";

const API_BASE = "http://localhost:8080/api/shipments";

// GET ALL SHIPMENTS
export const getAllShipments = () =>
    axios.get(API_BASE, {
        withCredentials: true
    });

// GET SHIPMENT BY ID
export const getShipmentById = (id) =>
    axios.get(`${API_BASE}/${id}`, {
        withCredentials: true
    });

// CREATE SHIPMENT
export const createShipment = (shipment) =>
    axios.post(`${API_BASE}/create`, shipment, {
        withCredentials: true
    });

// UPDATE SHIPMENT
export const updateShipment = (shipment) =>
    axios.put(API_BASE, shipment, {
        withCredentials: true
    });

// DELETE SHIPMENT
export const deleteShipment = (id) =>
    axios.delete(`${API_BASE}/${id}`, {
        withCredentials: true
    });