package ru.netology.FinalCloud.Users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.FinalCloud.FileCloud.repository.FileRepository;
import ru.netology.FinalCloud.Users.controller.UserController;
import ru.netology.FinalCloud.Users.models.JwtRequest;
import ru.netology.FinalCloud.Users.models.MyUserDetails;
import ru.netology.FinalCloud.Users.models.User;
import ru.netology.FinalCloud.Users.repository.UserRepo;
import ru.netology.FinalCloud.Users.service.AuthService;
import ru.netology.FinalCloud.Users.service.JWTBlackListService;
import ru.netology.FinalCloud.Users.service.JWTService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
    public class UsersControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private JWTService jwtService;

        @MockitoBean
        private AuthService authService;

        @MockitoBean
        private JWTBlackListService jWTBlackListService;

        @MockitoBean
        private UserRepo userRepo;

        @MockitoBean
        private FileRepository fileRepo;

        private MyUserDetails user;

        @Test
        public void testLogin_Success() throws Exception {

            JwtRequest request = new JwtRequest("username", "password");
            String token = "valid-jwt-token";
            when(authService.verify(any(JwtRequest.class))).thenReturn(token);


            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"username\", \"password\":\"password\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.auth-token").value(token));
        }

        @Test
        public void testLogin_Unauthorized() throws Exception {

            JwtRequest request = new JwtRequest("username", "wrongpassword"); // неверные данные
            when(authService.verify(any(JwtRequest.class))).thenReturn(null);


            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"username\", \"password\":\"wrongpassword\"}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void testLogout_Success() throws Exception {

            User basicUser = new User("test", "password");
            basicUser.setId(1);
            user = new MyUserDetails(basicUser);

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);


            String authToken = "Bearer valid-token";
            when(authService.verify(any())).thenReturn(authToken);

            mockMvc.perform(post("/logout")
                            .header("auth-token", authToken))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Successfully deactivated token" + authToken));

            verify(jWTBlackListService).deactivateToken("valid-token");
        }
    }
