package ru.practicum.mainservice.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateUserDTO {
    @Email
    @NotNull
    @NotBlank
    @Size(min = 6, max = 254)
    private String email;
    @NotNull
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}
