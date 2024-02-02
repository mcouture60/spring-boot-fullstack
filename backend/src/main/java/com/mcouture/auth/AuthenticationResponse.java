package com.mcouture.auth;

import com.mcouture.customer.CustomerDTO;

public record AuthenticationResponse (
        String jwtToken,
        CustomerDTO customerDTO
) {
}
