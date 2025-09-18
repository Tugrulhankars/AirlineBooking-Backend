package org.airlinebooking.airlinebooking.services;

import org.airlinebooking.airlinebooking.dtos.AirportDTO;
import org.airlinebooking.airlinebooking.dtos.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AirportService {
    Response<?> createAirport(AirportDTO airportDTO);
    Response<?> updateAirport(AirportDTO airportDTO);
    Response<List<AirportDTO>>  getAllAirports();
    Response<AirportDTO> getAirportById(Long id);
}
