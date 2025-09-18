package org.airlinebooking.airlinebooking.services;

import org.airlinebooking.airlinebooking.dtos.CreateFlightRequest;
import org.airlinebooking.airlinebooking.dtos.FlightDTO;
import org.airlinebooking.airlinebooking.dtos.Response;
import org.airlinebooking.airlinebooking.enums.City;
import org.airlinebooking.airlinebooking.enums.Country;
import org.airlinebooking.airlinebooking.enums.FlightStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public interface FlightService {
    Response<?> createFlight(CreateFlightRequest createFlightRequest);
    Response<FlightDTO> getFlightById(Long id);
    Response<List<FlightDTO>> getAllFlights();
    Response<?> updateFlight(CreateFlightRequest createFlightRequest);
    Response<List<FlightDTO>> searchFlight(String departurePortIata, String arrivalPortIata, FlightStatus status, LocalDate departureDate);
    Response<List<City>> getAllCities();
    Response<List<Country>> getAllCountries();
}
