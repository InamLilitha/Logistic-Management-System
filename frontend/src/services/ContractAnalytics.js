// Works out how many days are left on a contract (negative = already expired).
const daysUntilExpiry = (contract) => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const expiry = new Date(contract.contractExpiryDate + 'T00:00:00');
    return Math.ceil((expiry - today) / (1000 * 60 * 60 * 24));
};

// Every contract falls into exactly one of three groups.
export const getContractStatus = (contract) => {
    const days = daysUntilExpiry(contract);
    if (days < 0) return 'Expired';
    if (days <= 30) return 'Expiring Soon';
    return 'Active';
};

export const getContractAnalytics = (contracts) => {
    const count = (status) => contracts.filter(c => getContractStatus(c) === status).length;

    // The contract that will expire next (ignoring ones already expired).
    const nextToExpire = contracts
        .filter(c => getContractStatus(c) !== 'Expired')
        .sort((a, b) => a.contractExpiryDate.localeCompare(b.contractExpiryDate))[0] || null;

    return {
        total: contracts.length,
        active: count('Active'),
        expiringSoon: count('Expiring Soon'),
        expired: count('Expired'),
        nextToExpire
    };
};
