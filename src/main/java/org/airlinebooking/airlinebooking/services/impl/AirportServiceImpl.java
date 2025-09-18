package org.airlinebooking.airlinebooking.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.airlinebooking.airlinebooking.dtos.AirportDTO;
import org.airlinebooking.airlinebooking.dtos.Response;
import org.airlinebooking.airlinebooking.entities.Airport;
import org.airlinebooking.airlinebooking.enums.City;
import org.airlinebooking.airlinebooking.enums.Country;
import org.airlinebooking.airlinebooking.exceptions.BadRequestException;
import org.airlinebooking.airlinebooking.exceptions.NotFoundException;
import org.airlinebooking.airlinebooking.repositories.AirportRepository;
import org.airlinebooking.airlinebooking.services.AirportService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AirportServiceImpl implements AirportService {
    private final AirportRepository airportRepository;
    private final ModelMapper modelMapper;
    @Override
    public Response<?> createAirport(AirportDTO airportDTO) {
        log.info("Inside createAirport()");

        Country country = airportDTO.getCountry();
        City city = airportDTO.getCity();

        if (!city.getCountry().equals(country)){
            throw new BadRequestException("CITY does not belong to the country");
        }

        Airport airport = modelMapper.map(airportDTO, Airport.class);
        airportRepository.save(airport);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Airport Created Successfully")
                .build();
    }

    @Override
    public Response<?> updateAirport(AirportDTO airportDTO) {
        Long id = airportDTO.getId();

        Airport existingAirport = airportRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Airport Not Found"));


        if (airportDTO.getCity() != null){
            if (!airportDTO.getCity().getCountry().equals(existingAirport.getCountry())){
                throw new BadRequestException("CITY does not belong to the country");
            }
            existingAirport.setCity(airportDTO.getCity());
        }


        if (airportDTO.getName() != null){
            existingAirport.setName(airportDTO.getName());
        }

        if (airportDTO.getIataCode() != null){
            existingAirport.setIataCode(airportDTO.getIataCode());
        }

        airportRepository.save(existingAirport);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Airport updated Successfully")
                .build();
    }

    @Override
    public Response<List<AirportDTO>> getAllAirports() {
        List<AirportDTO> airports = airportRepository.findAll().stream()
                .map(airport -> modelMapper.map(airport, AirportDTO.class))
                .toList();

        return Response.<List<AirportDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(airports.isEmpty() ? "No Airports Found": "Airports retrieved successfully")
                .data(airports)
                .build();
    }

    @Override
    public Response<AirportDTO> getAirportById(Long id) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Airport Not Found"));

        AirportDTO airportDTO = modelMapper.map(airport, AirportDTO.class);

        return Response.<AirportDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message( "Airport retrieved successfully")
                .data(airportDTO)
                .build();
    }
}
