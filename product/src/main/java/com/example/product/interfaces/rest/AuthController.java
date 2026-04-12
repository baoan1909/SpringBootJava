package com.example.product.interfaces.rest;

import com.example.product.application.dto.command.LoginAccountCommand;
import com.example.product.application.dto.command.RegisterAccountCommand;
import com.example.product.application.dto.response.AccountResponse;
import com.example.product.application.dto.response.JwtAuthResponse;
import com.example.product.application.service.AuthApplicationService;
import com.example.product.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthApplicationService authApplicationService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, AuthApplicationService authApplicationService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public JwtAuthResponse login(@RequestBody LoginAccountCommand command) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                command.email(),
                command.password()
        );

        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return new JwtAuthResponse(jwt, "Bearer ");
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse register(@RequestBody RegisterAccountCommand command) {
        return authApplicationService.registerAccount(command);
    }
}
