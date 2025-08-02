package com.smartventure.smartventure.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ReactiveUserDetailsService uds;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthController(ReactiveUserDetailsService uds,
                          PasswordEncoder encoder,
                          JwtUtil jwtUtil) {
        this.uds = uds;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody Mono<AuthRequest> authMono) {
        return authMono.flatMap(req ->
                uds.findByUsername(req.username())
                        .filter(user -> encoder.matches(req.password(), user.getPassword()))
                        .map(user -> ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(user.getUsername()))))
                        .defaultIfEmpty(ResponseEntity.status(401).body(Map.of("error","Invalid credentials")))
        );
    }
}
