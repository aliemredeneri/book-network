package com.aed.booknetwork.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message = "First name is mandatory")
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    private String lastName;

    @NotEmpty(message = "Email name is mandatory")
    @NotBlank(message = "Email name is mandatory")
    @Email(message = "Email format is wrong")
    private String email;

    @NotEmpty(message = "password name is mandatory")
    @NotBlank(message = "password name is mandatory")
    @Size(min = 3)
    private String password;
}
