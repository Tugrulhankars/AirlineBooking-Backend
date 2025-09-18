package org.airlinebooking.airlinebooking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.airlinebooking.airlinebooking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {

    private Long id;

    private String bookingReference;

    private UserDTO user;

    private FlightDTO flight;

    private LocalDateTime bookingDate;

    private BookingStatus status;

    private List<PassengerDTO> passengers;
}

