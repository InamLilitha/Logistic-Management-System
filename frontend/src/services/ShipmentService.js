import axios from "axios";

const API_BASE = "http://localhost:8080/api/shipments";

export const getAllShipments = () =>
    axios.get(API_BASE);

export const getShipmentById = (id) =>
    axios.get(`${API_BASE}/${id}`);

export const createShipment = (shipment) =>
    axios.post(`${API_BASE}/create`, shipment);

export const updateShipment = (shipment) =>
    axios.put(API_BASE, shipment);

export const deleteShipment = (id) =>
    axios.delete(`${API_BASE}/${id}`);