package ru.netology.FinalCloud.Users.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;


//JWT-токен
@Service
public class JWTService {

    @Value("${secret.key}")
    private String secretKey;

    //получаем ключ
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //генерируем токен
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 10 * 1000))
                .signWith(getKey())
                .compact();
    }

    //вытаскиваем все клэймы
    private Claims extractAllClaims(String token) {
        token = token.trim().replaceAll("\\s+", "");
        System.out.println(token);
        JwtParserBuilder parser = Jwts.parser();
        parser.verifyWith(getKey());

        return parser.build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //вытаскиваем один клэйм
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    //вытаскиваем клэйм, а из клэйма вытаскиваем имя юзера
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //вытаскиваем клэйм, а из клэйма вытаскиваем срок истечения токена
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //проверка на истечение срока активности токена, true - если протухший
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractId(String token) {
        return extractClaim(token, Claims::getId);
    }


    //проверяем токен - userName, срок действия токена, есть ли в черном списке
    public boolean validateToken(String token, UserDetails userDetails) {
        String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
