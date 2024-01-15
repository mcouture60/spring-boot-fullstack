package com.mcouture.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
