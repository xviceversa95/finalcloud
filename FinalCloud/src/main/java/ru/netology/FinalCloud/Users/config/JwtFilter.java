package ru.netology.FinalCloud.Users.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.FinalCloud.Users.service.JWTBlackListService;
import ru.netology.FinalCloud.Users.service.JWTService;
import ru.netology.FinalCloud.Users.service.MyUserDetailsService;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JWTBlackListService jwtBlackListService;
    private JWTService jwtService;
    private MyUserDetailsService userDetailsService;

    public JwtFilter(JWTService jwtService, MyUserDetailsService userDetailsService, JWTBlackListService jwtBlackListService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.jwtBlackListService = jwtBlackListService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("auth-token");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUserName(token);
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(token, userDetails) && !jwtBlackListService.isInBlackList(token)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
