package org.airlinebooking.airlinebooking.repositories;

import org.airlinebooking.airlinebooking.entities.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailNotificationRepository extends JpaRepository<EmailNotification,Long> {
}
