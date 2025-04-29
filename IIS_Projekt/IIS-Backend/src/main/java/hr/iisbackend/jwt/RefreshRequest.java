package hr.iisbackend.jwt;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}