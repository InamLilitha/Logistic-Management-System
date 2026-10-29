
/* 24/08/2026
 * App.jsx
 * Logistic Management System
 */

import './index.css';
import React, { useState } from 'react';

import LmsLayout from './components/LmsLayout.jsx';
import Companies from './pages/Companies.jsx';
import Contracts from './pages/Contracts.jsx';
import Home from './pages/Home.jsx';
import Inventory from './pages/Inventory.jsx';
import Invoices from './pages/Invoices.jsx';
import Shipments from './pages/Shipments.jsx';
import ShipmentTracking from './pages/ShipmentTracking.jsx';

import Login from './auth/Login.jsx';
import CompanyRegister from './pages/CompanyRegister.jsx';
import UserManagement from './auth/UserManagement.jsx';
import Profile from './auth/Profile.jsx';

import { useAuth } from './auth/AuthContext';

function App() {
    const [activeTab, setActiveTab] = useState('home');
    const [showRegister, setShowRegister] = useState(false);

    const { currentUser, isAdmin, logout } = useAuth();

    const hasResetToken = new URLSearchParams(
        window.location.search
    ).has('token');

    if (hasResetToken) {
        return <Login onRegister={() => setShowRegister(true)} />;
    }

    if (!currentUser) {
        return showRegister ? (
            <CompanyRegister
                onBackToLogin={() => setShowRegister(false)}
            />
        ) : (
            <Login onRegister={() => setShowRegister(true)} />
        );
    }

    const renderContent = () => {
        switch (activeTab) {
            case 'companies':
                return <Companies canEdit={isAdmin} />;

            case 'contracts':
                return <Contracts canEdit={isAdmin} />;

            case 'home':
                return (
                    <Home
                        setActiveTab={setActiveTab}
                        canEdit={isAdmin}
                    />
                );

            case 'inventory':
                return <Inventory />;

            case 'invoices':
                return <Invoices canEdit={isAdmin} />;

            case 'shipments':
                return <Shipments canEdit={isAdmin} />;

            case 'tracking':
                return <ShipmentTracking canEdit={isAdmin} />;

            case 'users':
                return isAdmin ? (
                    <UserManagement />
                ) : (
                    <Home
                        setActiveTab={setActiveTab}
                        canEdit={false}
                    />
                );

            case 'profile':
                return <Profile />;

            default:
                return (
                    <Home
                        setActiveTab={setActiveTab}
                        canEdit={isAdmin}
                    />
                );
        }
    };

    return (
        <LmsLayout
            activeTab={activeTab}
            setActiveTab={setActiveTab}
            currentUser={currentUser}
            isAdmin={isAdmin}
            logout={logout}
        >
            {renderContent()}
        </LmsLayout>
    );
}

export default App;
