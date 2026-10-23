/* 24/08/2026
 *App.jsx
 *App in services folder
 *Maghdie Petersen 230600204
 *  */

import './index.css';
import React, {useState} from 'react';
import LmsLayout from './components/LmsLayout.jsx';
import Companies from './pages/Companies.jsx';
import Home from './pages/Home.jsx';
import Inventory from './pages/Inventory.jsx';
import Invoices from './pages/Invoices.jsx';
import Shipments from './pages/Shipments.jsx';
import ShipmentTracking from './pages/ShipmentTracking.jsx';

function App() {
    const [activeTab, setActiveTab] = useState('home');

    const renderContent = () => {
        switch (activeTab){
            case 'companies': return <Companies />
            case 'home': return <Home setActiveTab={setActiveTab} />
            case 'inventory': return <Inventory />
            case 'invoices': return <Invoices />
            case 'shipments': return <Shipments />
            case 'tracking': return <ShipmentTracking />
            default: return <Home />
        }
    };

  return (
    <LmsLayout activeTab={activeTab} setActiveTab={setActiveTab}>
        {renderContent()}
    </LmsLayout>
  );
}

export default App;
