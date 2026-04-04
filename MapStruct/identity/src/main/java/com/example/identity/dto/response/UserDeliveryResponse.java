package com.example.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeliveryResponse {
    private String username;
    private String firstname;
    private String lastname;

    private String streetName;
    private String cityName;
}
