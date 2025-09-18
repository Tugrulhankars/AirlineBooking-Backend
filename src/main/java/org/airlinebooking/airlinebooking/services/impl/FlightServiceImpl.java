package org.airlinebooking.airlinebooking.services.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.airlinebooking.airlinebooking.dtos.CreateFlightRequest;
import org.airlinebooking.airlinebooking.dtos.FlightDTO;
import org.airlinebooking.airlinebooking.dtos.Response;
import org.airlinebooking.airlinebooking.entities.Airport;
import org.airlinebooking.airlinebooking.entities.Flight;
import org.airlinebooking.airlinebooking.entities.User;
import org.airlinebooking.airlinebooking.enums.City;
import org.airlinebooking.airlinebooking.enums.Country;
import org.airlinebooking.airlinebooking.enums.FlightStatus;
import org.airlinebooking.airlinebooking.exceptions.BadRequestException;
import org.airlinebooking.airlinebooking.exceptions.NotFoundException;
import org.airlinebooking.airlinebooking.repositories.AirportRepository;
import org.airlinebooking.airlinebooking.repositories.FlightRepository;
import org.airlinebooking.airlinebooking.repositories.UserRepository;
import org.airlinebooking.airlinebooking.services.FlightService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {
    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public Response<?> createFlight(CreateFlightRequest createFlightRequest) {
        if (createFlightRequest.getArrivalTime().isBefore(createFlightRequest.getDepartureTime())) {
            throw new BadRequestException("Arrival Time cannot be before the departure time");
        }

        if (flightRepository.existsByFlightNumber(createFlightRequest.getFlightNumber())) {
            throw new BadRequestException("Flight with this number already exists");
        }

        //fetch and validate the departure airport
        Airport departureAirport = airportRepository.findByIataCode(createFlightRequest.getDepartureAirportIataCode())
                .orElseThrow(() -> new NotFoundException("Departure Airport Not Found"));

        //fetch and validate the departure airport
        Airport arrivalAirport = airportRepository.findByIataCode(createFlightRequest.getArrivalAirportIataCode())
                .orElseThrow(() -> new NotFoundException("Arrival Airport Not Found"));


        Flight flightToSave = new Flight();
        flightToSave.setFlightNumber(createFlightRequest.getFlightNumber());
        flightToSave.setDepartureAirport(departureAirport);
        flightToSave.setArrivalAirport(arrivalAirport);
        flightToSave.setDepartureTime(createFlightRequest.getDepartureTime());
        flightToSave.setArrivalTime(createFlightRequest.getArrivalTime());
        flightToSave.setBasePrice(createFlightRequest.getBasePrice());
        flightToSave.setStatus(FlightStatus.SCHEDULED);

        //assign pilot to the flight(get and validate the pilot)
        if (createFlightRequest.getPilotId() != null){

            User pilot = userRepository.findById(createFlightRequest.getPilotId())
                    .orElseThrow(()-> new NotFoundException("Pilot is not found"));

            boolean isPilot = pilot.getRoles().stream()
                    .anyMatch(role -> role.getName().equalsIgnoreCase("PILOT"));

            if (!isPilot){
                throw new BadRequestException("Claimed User-Pilot not a certified pilot");
            }
            flightToSave.setAssignedPilot(pilot);
        }

        //save the flight
        flightRepository.save(flightToSave);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("FLight saved successfully")
                .build();
    }

    @Override
    public Response<FlightDTO> getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("FLight Not Found"));

        FlightDTO flightDTO = modelMapper.map(flight, FlightDTO.class);

        if (flightDTO.getBookings() != null){
            flightDTO.getBookings().forEach(bookingDTO -> bookingDTO.setFlight(null));
        }

        return Response.<FlightDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Flight retrieved Successfully")
                .data(flightDTO)
                .build();
    }

    @Override
    public Response<List<FlightDTO>> getAllFlights() {
        Sort sortByIdDesc=Sort.by(Sort.Direction.DESC,"id");
        List<FlightDTO> flights = flightRepository.findAll(sortByIdDesc).stream()
                .map(flight -> {
                    FlightDTO flightDTO = modelMapper.map(flight, FlightDTO.class);
                    if (flightDTO.getBookings() != null){
                        flightDTO.getBookings().forEach(bookingDTO -> bookingDTO.setFlight(null));
                    }
                    return flightDTO;
                }).toList();

        return Response.<List<FlightDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(flights.isEmpty() ? "No Flights Found" : "Flights retrieved successfully")
                .data(flights)
                .build();
    }

    @Override
    @Transactional
    public Response<?> updateFlight(CreateFlightRequest flightRequest) {
        Long id = flightRequest.getId();
        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Flight Not Found"));

        if (flightRequest.getDepartureTime() != null){
            existingFlight.setDepartureTime(flightRequest.getDepartureTime());
        }

        if (flightRequest.getArrivalTime() != null){
            existingFlight.setArrivalTime(flightRequest.getArrivalTime());
        }

        if (flightRequest.getBasePrice() != null){
            existingFlight.setBasePrice(flightRequest.getBasePrice());
        }

        if (flightRequest.getStatus() != null){
            existingFlight.setStatus(flightRequest.getStatus());
        }

        //if pilot id is passed in validate the pilot and update it

        if (flightRequest.getPilotId() != null){

            User pilot = userRepository.findById(flightRequest.getPilotId())
                    .orElseThrow(()-> new NotFoundException("Pilot is not found"));

            boolean isPilot = pilot.getRoles().stream()
                    .anyMatch(role -> role.getName().equalsIgnoreCase("PILOT"));

            if (!isPilot){
                throw new BadRequestException("Claimed User-Pilot not a certified pilot");
            }
            existingFlight.setAssignedPilot(pilot);
        }

        flightRepository.save(existingFlight);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("FLight Updated Successfully")
                .build();

    }

    @Override
    public Response<List<FlightDTO>> searchFlight(String departurePortIata, String arrivalPortIata, FlightStatus status, LocalDate departureDate) {

        // Define the start and end of the day for the given departureDate
        LocalDateTime startOfDay = departureDate.atStartOfDay();
        LocalDateTime endOfDay = departureDate.plusDays(1).atStartOfDay().minusNanos(1); // End of day (23:59:59.999...)

        List<Flight> flights = flightRepository.findByDepartureAirportIataCodeAndArrivalAirportIataCodeAndStatusAndDepartureTimeBetween(
                departurePortIata, arrivalPortIata, status, startOfDay, endOfDay
        );

        List<FlightDTO> flightDTOS = flights.stream()
                .map(flight -> {
                    FlightDTO flightDTO = modelMapper.map(flight, FlightDTO.class);
                    flightDTO.setAssignedPilot(null);
                    flightDTO.setBookings(null);
                    return flightDTO;
                }).toList();


        return Response.<List<FlightDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(flightDTOS.isEmpty() ? "No FLights Found": "FLight retreived sucessfully")
                .data(flightDTOS)
                .build();
    }

    @Override
    public Response<List<City>> getAllCities() {
        return Response.<List<City>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("success")
                .data(List.of(City.values()))
                .build();
    }

    @Override
    public Response<List<Country>> getAllCountries() {
        return Response.<List<Country>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("success")
                .data(List.of(Country.values()))
                .build();
    }
}
