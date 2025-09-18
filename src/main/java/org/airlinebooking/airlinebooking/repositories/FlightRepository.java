package org.airlinebooking.airlinebooking.repositories;

import org.airlinebooking.airlinebooking.entities.Flight;
import org.airlinebooking.airlinebooking.enums.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface FlightRepository extends JpaRepository<Flight,Long> {
    boolean existsByFlightNumber(String flightNumber);

    List<Flight> findByDepartureAirportIataCodeAndArrivalAirportIataCodeAndStatusAndDepartureTimeBetween(
            String departureIataCode, String arrivalIataCode, FlightStatus status, LocalDateTime startOfDay, LocalDateTime endOfDay);

}
