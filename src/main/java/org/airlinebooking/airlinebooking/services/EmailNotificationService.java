package org.airlinebooking.airlinebooking.services;

import org.airlinebooking.airlinebooking.entities.Booking;
import org.airlinebooking.airlinebooking.entities.User;
import org.springframework.stereotype.Service;

@Service

public interface EmailNotificationService {
    void sendBookingTickerEmail(Booking booking);
    void sendWelcomeEmail(User user);
}
