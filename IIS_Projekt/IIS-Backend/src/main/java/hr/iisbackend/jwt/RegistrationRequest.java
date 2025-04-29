package hr.iisbackend.jwt;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String username;
    private String password;
}
