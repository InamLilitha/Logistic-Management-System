/*
 * LmsLayout.jsx
 * Logistic Management System
 */

import React, { useState } from 'react';

import {
    LayoutDashboard,
    Truck,
    Package,
    Building2,
    FileText,
    ClipboardClock,
    FileSignature,
    Users,
    UserCircle,
    LogOut,
    Menu,
    X,
    ChevronRight,
    Bell,
    Boxes,
    ShieldCheck,
} from 'lucide-react';

const LmsLayout = ({
    children,
    activeTab,
    setActiveTab,
    currentUser,
    isAdmin,
    logout,
}) => {
    const [sidebarOpen, setSidebarOpen] = useState(false);

    const navItems = [
        { id: 'home', label: 'Dashboard', icon: LayoutDashboard },
        { id: 'companies', label: 'Companies', icon: Building2 },
        { id: 'inventory', label: 'Inventory', icon: Package },
        { id: 'contracts', label: 'Contracts', icon: FileSignature },
        { id: 'shipments', label: 'Shipments', icon: Truck },
        { id: 'tracking', label: 'Shipment Tracking', icon: ClipboardClock },
        { id: 'invoices', label: 'Invoices', icon: FileText },
        ...(isAdmin
            ? [{ id: 'users', label: 'Staff Profiles', icon: Users }]
            : []),
        { id: 'profile', label: 'My Profile', icon: UserCircle },
    ];

    const activeItem =
        navItems.find((item) => item.id === activeTab) || navItems[0];

    const displayName =
        currentUser?.name ||
        currentUser?.username ||
        currentUser?.email ||
        'User';

    const displayRole = String(currentUser?.role || 'Staff')
        .replace(/^ROLE_/i, '')
        .replaceAll('_', ' ');

    const initials =
        displayName
            .split(' ')
            .filter(Boolean)
            .slice(0, 2)
            .map((part) => part.charAt(0).toUpperCase())
            .join('') || 'U';

    const handleNavigation = (tabId) => {
        setActiveTab(tabId);
        setSidebarOpen(false);
    };

    return (
        <div className="lms-app">
            {sidebarOpen && (
                <button
                    type="button"
                    className="lms-mobile-overlay"
                    aria-label="Close navigation menu"
                    onClick={() => setSidebarOpen(false)}
                />
            )}

            <aside
                className={`lms-sidebar ${
    sidebarOpen ? 'lms-sidebar-open' : ''
}`}
            >
                <div className="lms-brand">
                    <div className="lms-brand-icon">
                        <Truck size={24} strokeWidth={2.2} />
                    </div>

                    <div className="lms-brand-copy">
                        <h1>ModuFlow</h1>
                        <span>LOGISTICS MANAGEMENT</span>
                    </div>

                    <button
                        type="button"
                        className="lms-mobile-close"
                        aria-label="Close navigation"
                        onClick={() => setSidebarOpen(false)}
                    >
                        <X size={20} />
                    </button>
                </div>

                <div className="lms-sidebar-content">
                    <p className="lms-nav-heading">MAIN MENU</p>

                    <nav className="lms-nav" aria-label="Main navigation">
                        {navItems.map((item) => {
                            const Icon = item.icon;
                            const isActive = activeTab === item.id;

                            return (
                                <button
                                    key={item.id}
                                    type="button"
                                    onClick={() => handleNavigation(item.id)}
                                    className={`lms-nav-item ${
    isActive ? 'lms-nav-active' : ''
}`}
                                    aria-current={
                                        isActive ? 'page' : undefined
                                    }
                                >
                                    <Icon
                                        size={19}
                                        strokeWidth={isActive ? 2.2 : 1.8}
                                    />

                                    <span>{item.label}</span>

                                    {isActive && (
                                        <ChevronRight
                                            size={16}
                                            className="lms-nav-chevron"
                                        />
                                    )}
                                </button>
                            );
                        })}
                    </nav>

                    <div className="lms-sidebar-tip">
                        <div className="lms-tip-icon">
                            <Boxes size={21} />
                        </div>

                        <p className="lms-tip-title">
                            Everything in motion.
                        </p>

                        <p className="lms-tip-text">
                            Manage your shipments, inventory, contracts and
                            logistics operations in one place.
                        </p>

                        <div className="lms-tip-status">
                            <span className="lms-status-dot" />
                            Workspace ready
                        </div>
                    </div>
                </div>

                <div className="lms-sidebar-footer">
                    <div className="lms-user-profile">
                        <div className="lms-avatar">
                            {initials}
                        </div>

                        <div className="lms-user-info">
                            <p className="lms-user-name">{displayName}</p>

                            <span className="lms-user-role">
                                {displayRole}
                            </span>
                        </div>

                        {isAdmin && (
                            <span
                                className="lms-admin-indicator"
                                title="Administrator"
                                aria-label="Administrator"
                            >
                                <ShieldCheck size={16} />
                            </span>
                        )}
                    </div>

                    <button
                        type="button"
                        onClick={logout}
                        className="lms-logout-button"
                    >
                        <LogOut size={17} />
                        <span>Sign out</span>
                    </button>
                </div>
            </aside>

            <div className="lms-main">
                <header className="lms-topbar">
                    <div className="lms-topbar-left">
                        <button
                            type="button"
                            className="lms-mobile-menu"
                            aria-label="Open navigation menu"
                            onClick={() => setSidebarOpen(true)}
                        >
                            <Menu size={22} />
                        </button>

                        <div className="lms-page-heading">
                            <div className="lms-breadcrumb">
                                <span>Workspace</span>
                                <ChevronRight size={14} />

                                <span className="lms-breadcrumb-current">
                                    {activeItem.label}
                                </span>
                            </div>

                            <h2>{activeItem.label}</h2>
                        </div>
                    </div>

                    <div className="lms-topbar-right">
                        <div className="lms-system-status">
                            <span className="lms-status-dot" />
                            <span>Workspace</span>
                        </div>

                        <div className="lms-topbar-divider" />

                        <button
                            type="button"
                            className="lms-header-icon"
                            aria-label="Notifications"
                            title="Notifications"
                            onClick={() => {
                                // Notifications can be connected later.
                            }}
                        >
                            <Bell size={19} />
                        </button>

                        <button
                            type="button"
                            className="lms-topbar-user"
                            onClick={() => handleNavigation('profile')}
                            title="Open your profile"
                            aria-label="Open your profile"
                        >
                            <div className="lms-avatar lms-avatar-small">
                                {initials}
                            </div>
                        </button>
                    </div>
                </header>

                <main className="lms-content">{children}</main>

                <footer className="lms-footer">
                    <span>
                        <strong>ModuFlow</strong> · Logistics Management
                        System
                    </span>

                    <span>Built for smoother operations.</span>
                </footer>
            </div>
        </div>
    );
};

export default LmsLayout;

