package org.airlinebooking.airlinebooking.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.airlinebooking.airlinebooking.enums.City;
import org.airlinebooking.airlinebooking.enums.Country;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportDTO {

    private Long id;

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = " City is required")
    private City city;

    @NotNull(message = "Country is required")
    private Country country;

    @NotBlank(message = "Iata Code is required")
    private String iataCode;

}
