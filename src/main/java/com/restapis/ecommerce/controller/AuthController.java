package com.restapis.ecommerce.controller;

import com.restapis.ecommerce.io.AuthenticationRequest;
import com.restapis.ecommerce.io.AuthenticationResponse;
import com.restapis.ecommerce.service.AppUserDetailsService;
import com.restapis.ecommerce.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private  AuthenticationManager authenticationManager;
    private  AppUserDetailsService userDetailsService;
    private  JwtUtil jwtUtil;

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getEmail());

        String token = jwtUtil.generateToken(userDetails);

        return new AuthenticationResponse(request.getEmail(), token);
    }
}
