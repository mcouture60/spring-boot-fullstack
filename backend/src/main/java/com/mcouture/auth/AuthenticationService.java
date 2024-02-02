package com.mcouture.auth;

import com.mcouture.customer.Customer;
import com.mcouture.customer.CustomerDTO;
import com.mcouture.customer.CustomerDTOMapper;
import com.mcouture.jwt.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final CustomerDTOMapper customerDTOMapper;
    private final JWTUtil jwtUtil;

    public AuthenticationService(AuthenticationManager authenticationManager, CustomerDTOMapper customerDTOMapper, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customerDTOMapper = customerDTOMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        Customer customer = (Customer) authentication.getPrincipal();
        CustomerDTO customerDTO = customerDTOMapper.apply(customer);
        String jwtToken = jwtUtil.issueToken(
                customerDTO.username(),
                customerDTO.roles()
        );
        return new AuthenticationResponse(jwtToken, customerDTO);
    }
}
