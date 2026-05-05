package ru.netology.FinalCloud.Users.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.FinalCloud.Users.models.JwtRequest;
import ru.netology.FinalCloud.Users.models.JwtResponse;
import ru.netology.FinalCloud.Users.service.AuthService;
import ru.netology.FinalCloud.Users.service.JWTBlackListService;
import ru.netology.FinalCloud.Users.service.JWTService;

@RestController
public class UserController {

    private final JWTBlackListService jWTBlackListService;
    private JWTService jwtService;
    private AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(JWTService jwtService, AuthService authService, JWTBlackListService jWTBlackListService) {
        this.jwtService = jwtService;
        this.authService = authService;
        this.jWTBlackListService = jWTBlackListService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        logger.info("Запрос на авторизацию");
        String token = authService.verify(request);
        logger.info("Токен получен: " + token);
        if (token == null) {
            logger.info("Проблема с токеном, авторизация не прошла");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        JwtResponse response = new JwtResponse(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken) {
        logger.info("Получили запрос на разлогин");
        if (authToken != null && authToken.startsWith("Bearer ")) {
            jWTBlackListService.deactivateToken(authToken.substring(7));
        }
        logger.info("Успешно разлогинили пользователя");
        return ResponseEntity.ok("Successfully deactivated token" + authToken);
    }
}
