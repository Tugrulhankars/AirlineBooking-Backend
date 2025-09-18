package org.airlinebooking.airlinebooking.services;

import org.airlinebooking.airlinebooking.dtos.BookingDTO;
import org.airlinebooking.airlinebooking.dtos.CreateBookingRequest;
import org.airlinebooking.airlinebooking.dtos.Response;
import org.airlinebooking.airlinebooking.enums.BookingStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookingService {
    Response<?> createBooking(CreateBookingRequest createBookingRequest);
    Response<BookingDTO> getBookingById(Long id);
    Response<List<BookingDTO>> getAllBookings();
    Response<List<BookingDTO>> getMyBookings();
    Response<?> updateBookingStatus(Long id, BookingStatus status);
}
