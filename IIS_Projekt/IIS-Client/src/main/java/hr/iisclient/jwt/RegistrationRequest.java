package hr.iisclient.jwt;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter @Setter
public class RegistrationRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}