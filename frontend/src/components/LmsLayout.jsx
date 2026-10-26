/* 24/08/2026
 *Lmslayout.jsx
 *LmsLayout in components folder
 *Maghdie Petersen 230600204
 *  */

import React from 'react';
import {LayoutDashboard, Truck, Package, Building2, FileText, ClipboardClock, FileSignature, Users, UserCircle, LogOut} from 'lucide-react';

const LmsLayout = ({children, activeTab, setActiveTab, currentUser, isAdmin, logout}) => {
    const navItems = [
        {id: 'home', label: 'Dashboard', icon: LayoutDashboard},
        {id: 'companies', label: 'Companies', icon: Building2},
        {id: 'inventory', label: 'Inventory', icon: Package},
        {id: 'contracts', label: 'Contracts', icon: FileSignature},
        {id: 'shipments', label: 'Shipments', icon: Truck},
        {id: 'tracking', label: 'ShipmentTracking', icon: ClipboardClock},
        {id: 'invoices', label: 'Invoices', icon: FileText},
        ...(isAdmin ? [{id: 'users', label: 'Staff Profiles', icon: Users}] : []),
        {id: 'profile', label: 'My Profile', icon: UserCircle},
    ];

    return(
        <div className="flex h-screen bg-lms-bg overflow-hidden">
            <aside className="w-64 bg-lms-navy text-white flex flex-col justify-between shrink-0">
                <div>
                    <div className="p-6 border-b border-blue-900/50 flex items-center gap-3">
                        <Truck className="w-6 h-6 text-lms-ice"/>
                        <h1 className="font-bold tracking-wide text-base">LMS Platform</h1>
                    </div>
                    <nav className="mt-6 px-3 space-y-1">
                        {navItems.map((item) => {
                            const Icon = item.icon;
                            return (
                                <button
                                key = {item.id}
                                onClick={() => setActiveTab(item.id)}
                                className={`w-full flex items-center gap-3 px-4 rounded-md text-sm font-medium transition ${
                                    activeTab=== item.id ? 'bg-lms-action text-white' : 'text-lms-ice/80 hover:bg-blue-900/50'}`}
                                >
                                    <Icon className="w-5 h-5"/>
                                    <span>{item.label}</span>
                                </button>
                            );
                        })}
                    </nav>
                </div>
                <div className="border-t border-blue-900/50 p-4">
                    <p className="truncate text-sm font-semibold">{currentUser.name}</p>
                    <p className="text-xs uppercase tracking-wide text-lms-ice">{currentUser.role}</p>
                    <button onClick={logout} className="mt-4 flex w-full items-center gap-2 rounded px-2 py-2 text-sm text-lms-ice hover:bg-blue-900/50">
                        <LogOut className="h-4 w-4" /> Sign out
                    </button>
                </div>
            </aside>

            {/* Content Area */}
            <div className="flex-1 flex flex-col overflow-hidden">
                <header className="h-16 bg-white border-b border-slate-200 px-8 flex items-center justify-between shadow-sm">
                    <span className="font-semibold text-lms-textMain capitalize">{activeTab} Section</span>
                    <div className="flex items-center gap-2 px-3 py-1 bg-green-50 text-lms-success rounded-full text-xs font-semibold">
                        <span className="w-2 h-2 rounded-full bg-lms-success animate-pulse"></span>
                        Backend Online
                    </div>
                </header>
                <main className="flex-1 overflow-y-auto">{children}</main>
            </div>
        </div>
    );
};

export default LmsLayout;
