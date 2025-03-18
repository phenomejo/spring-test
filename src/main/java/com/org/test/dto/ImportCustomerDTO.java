package com.org.test.dto;

import com.univocity.parsers.annotations.Parsed;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportCustomerDTO {

    @Parsed(field = "firstName")
    private String firstName;
    @Parsed(field = "lastName")
    private String lastName;

    @Parsed(field = "email")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;
}
