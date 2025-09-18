package org.airlinebooking.airlinebooking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.airlinebooking.airlinebooking.enums.PassengerType;

@Entity
@Data
@Table(name = "passengers")
@AllArgsConstructor
@NoArgsConstructor
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private String firstName;
    private String lastName;
    private String passportNumber;

    @Enumerated(EnumType.STRING)
    private PassengerType type;

    private String seatNumber;
    private String specialRequests;
}
