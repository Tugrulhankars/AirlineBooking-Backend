package org.airlinebooking.airlinebooking.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.airlinebooking.airlinebooking.enums.PassengerType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerDTO {

    private Long id;

    @NotBlank(message = "First Name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last Name cannot be blank")
    private String lastName;

    private String passportNumber;

    @NotNull(message = "Passenger Type cannot be null")
    private PassengerType type;

    private String seatNumber;

    private String specialRequests;

}
