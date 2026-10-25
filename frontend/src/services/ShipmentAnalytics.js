export const getShipmentAnalytics = (shipments) => {
    const totalShipments = shipments.length;

    const routeCounts = shipments.reduce((routes, shipment) => {
        const route = `${shipment.origin} → ${shipment.destination}`;

        routes[route] = (routes[route] || 0) + 1;

        return routes;
    }, {});

    const mostCommonRoute =
        Object.keys(routeCounts).length > 0
            ? Object.keys(routeCounts).reduce((a, b) =>
                routeCounts[a] >= routeCounts[b] ? a : b
            )
            : "No shipments yet";

    const mostCommonRouteCount =
        Object.keys(routeCounts).length > 0
            ? routeCounts[mostCommonRoute]
            : 0;

    return {
        totalShipments,
        mostCommonRoute,
        mostCommonRouteCount
    };
};