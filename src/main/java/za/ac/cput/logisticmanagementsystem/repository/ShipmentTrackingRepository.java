package za.ac.cput.logisticmanagementsystem.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.logisticmanagementsystem.domain.ShipmentTracking;

@Repository
public interface ShipmentTrackingRepository extends JpaRepository<ShipmentTracking, String> {

}
