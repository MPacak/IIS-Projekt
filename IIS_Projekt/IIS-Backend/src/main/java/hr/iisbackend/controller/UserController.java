package hr.iisbackend.controller;
import hr.iisbackend.jwt.*;
import hr.iisbackend.model.User;
import hr.iisbackend.service.JwtService;
import hr.iisbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest req) {
        if (userService.findByUsername(req.getUsername()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username already taken");
        }
        User newUser = new User();
        newUser.setUsername(req.getUsername());
        newUser.setPassword(passwordEncoder.encode(req.getPassword()));
        userService.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req) {
        User u = userService.findByUsername(req.getUsername());
        if (u != null && userService.checkPassword(req.getPassword(), u.getPassword())) {
            String accessToken = jwtService.generateAccessToken(u.getUsername());
            String refreshToken = jwtService.generateRefreshToken(u.getUsername());
            return new TokenResponse(accessToken, refreshToken);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody RefreshRequest r) {
        if (!jwtService.validateToken(r.getRefreshToken()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh");
        String user = jwtService.getUsernameFromToken(r.getRefreshToken());
        // (Optionally check against a store of issued refresh-tokens here)
        String at = jwtService.generateAccessToken(user);
        // you may choose to issue a new refresh, or keep the old one
        String rt = jwtService.generateRefreshToken(user);
        return new TokenResponse(at, rt);
    }
}
