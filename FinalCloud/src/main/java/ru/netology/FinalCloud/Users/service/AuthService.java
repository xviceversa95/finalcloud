package ru.netology.FinalCloud.Users.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.netology.FinalCloud.Users.models.JwtRequest;


@Service
public class AuthService {

    private JWTService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    AuthenticationManager authManager;

    @Autowired
    public AuthService(JWTService jwtService, AuthenticationManager authManager) {
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    //проверяем, если юзер ввел верные логин и пароль - генерируем токен
    // и кладем в Security Context объект Authentification с инфой о текущем юзере
    public String verify (JwtRequest request) {
        System.out.println("Verify User");
        try {
            Authentication authentication =
                    authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(request.getLogin());
                System.out.println("token: " + token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return token;
            } else {
                logger.info("Bad authentication");
                return null;
            }
        } catch (BadCredentialsException ex) {
            return null;
        }
    }

}

