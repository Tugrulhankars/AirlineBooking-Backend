package org.airlinebooking.airlinebooking.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.airlinebooking.airlinebooking.dtos.BookingDTO;
import org.airlinebooking.airlinebooking.dtos.CreateBookingRequest;
import org.airlinebooking.airlinebooking.dtos.Response;
import org.airlinebooking.airlinebooking.entities.Booking;
import org.airlinebooking.airlinebooking.entities.Flight;
import org.airlinebooking.airlinebooking.entities.Passenger;
import org.airlinebooking.airlinebooking.entities.User;
import org.airlinebooking.airlinebooking.enums.BookingStatus;
import org.airlinebooking.airlinebooking.enums.FlightStatus;
import org.airlinebooking.airlinebooking.exceptions.BadRequestException;
import org.airlinebooking.airlinebooking.exceptions.NotFoundException;
import org.airlinebooking.airlinebooking.repositories.BookingRepository;
import org.airlinebooking.airlinebooking.repositories.FlightRepository;
import org.airlinebooking.airlinebooking.repositories.PassengerRepository;
import org.airlinebooking.airlinebooking.services.BookingService;
import org.airlinebooking.airlinebooking.services.EmailNotificationService;
import org.airlinebooking.airlinebooking.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;
    private final EmailNotificationService emailNotificationService;
    @Override
    @Transactional
    public Response<?> createBooking(CreateBookingRequest createBookingRequest) {

        User user = userService.currentUser();

        Flight flight = flightRepository.findById(createBookingRequest.getFlightId())
                .orElseThrow(()-> new NotFoundException("Flight Not Found"));

        if (flight.getStatus() != FlightStatus.SCHEDULED){
            throw new BadRequestException("You can only book a flight that is scheduled");
        }

        Booking booking = new Booking();
        booking.setBookingReference(generateBookingReference());
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        if (createBookingRequest.getPassengers() != null && !createBookingRequest.getPassengers().isEmpty()){

            List<Passenger> passengers = createBookingRequest.getPassengers().stream()
                    .map(passengerDTO -> {
                        Passenger passenger = modelMapper.map(passengerDTO, Passenger.class);
                        passenger.setBooking(savedBooking);
                        return passenger;
                    }).toList();

            passengerRepository.saveAll(passengers);
            savedBooking.setPassengers(passengers);
        }

        //SEND EMAIL TICKER OUT
        emailNotificationService.sendBookingTickerEmail(savedBooking);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Booking created successfully")
                .build();

    }

    @Override
    public Response<BookingDTO> getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Booking not found"));

        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
        bookingDTO.getFlight().setBookings(null);

        return Response.<BookingDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Booking retreived successfully")
                .data(bookingDTO)
                .build();
    }

    @Override
    public Response<List<BookingDTO>> getAllBookings() {

        List<Booking> allBookings = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<BookingDTO> bookings = allBookings.stream()
                .map(booking -> {
                    BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
                    bookingDTO.getFlight().setBookings(null);
                    return bookingDTO;
                }).toList();

        return Response.<List<BookingDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(bookings.isEmpty()? "No Booking Found" : "Booking retreived successfully")
                .data(bookings)
                .build();
    }

    @Override
    public Response<List<BookingDTO>> getMyBookings() {
        User user = userService.currentUser();
        List<Booking> userBookings =bookingRepository.findByUserIdOrderByIdDesc(user.getId());


        List<BookingDTO> bookings = userBookings.stream()
                .map(booking -> {
                    BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
                    bookingDTO.getFlight().setBookings(null);
                    return bookingDTO;
                }).toList();

        return Response.<List<BookingDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(bookings.isEmpty()? "No Booking Found for this user" : "User Bookings retrieved successfully")
                .data(bookings)
                .build();

    }

    @Override
    @Transactional
    public Response<?> updateBookingStatus(Long id, BookingStatus status) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Booking Not Found"));

        booking.setStatus(status);
        bookingRepository.save(booking);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Booking Updated Successfully")
                .build();

    }

    private String generateBookingReference(){
        return UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }
}
