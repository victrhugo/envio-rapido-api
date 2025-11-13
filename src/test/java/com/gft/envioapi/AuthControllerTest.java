package com.gft.envioapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gft.envioapi.controller.AuthController;
import com.gft.envioapi.dto.LoginRequest;
import com.gft.envioapi.dto.LoginResponse;
import com.gft.envioapi.entity.User;
import com.gft.envioapi.entity.UserRole;
import com.gft.envioapi.exception.GlobalExceptionHandler;
import com.gft.envioapi.security.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_DeveRetornar200_QuandoCredenciaisValidas() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setLogin("erika");
        req.setSenha("123");

        LoginResponse resp = new LoginResponse("jwt-token-abc", "erika");
        when(authService.login(any(LoginRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-abc"))
                .andExpect(jsonPath("$.login").value("erika"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_DeveRetornar500_QuandoUsuarioNaoEncontrado() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setLogin("naoexiste");
        req.setSenha("x");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_DeveRetornar500_QuandoSenhaInvalida() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setLogin("erika");
        req.setSenha("errada");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Senha inválida"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void register_DeveRetornar200_QuandoDadosValidos() throws Exception {
        User entrada = new User(null, "erika", "123", UserRole.USER);
        User salvo   = new User(1L, "erika", "hash", UserRole.USER);

        when(authService.registrar(any(User.class))).thenReturn(salvo);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.login").value("erika"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(authService, times(1)).registrar(any(User.class));
    }

    @Test
    void register_DeveRetornar500_QuandoServiceLancarExcecao() throws Exception {
        User entrada = new User(null, "erika", "123", UserRole.USER);
        when(authService.registrar(any(User.class)))
                .thenThrow(new RuntimeException("Falha ao salvar"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isInternalServerError());

        verify(authService, times(1)).registrar(any(User.class));
    }
}
