package org.airlinebooking.airlinebooking.repositories;

import org.airlinebooking.airlinebooking.entities.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport,Long> {
    Optional<Airport> findByIataCode(String iataCode);

}
