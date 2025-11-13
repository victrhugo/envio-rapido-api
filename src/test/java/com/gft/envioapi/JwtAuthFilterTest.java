package com.gft.envioapi;

import com.gft.envioapi.security.JwtService;
import com.gft.envioapi.security.filter.JwtAuthFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthFilter filter;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveIgnorarRotasDeAuth() throws Exception {
        var req = new MockHttpServletRequest("POST", "/auth/login");
        var res = new MockHttpServletResponse();
        var chain = spy(new MockFilterChain());

        filter.doFilter(req, res, chain);

        verifyNoInteractions(jwtService);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void deveIgnorarRotaError() throws Exception {
        var req = new MockHttpServletRequest("GET", "/error");
        var res = new MockHttpServletResponse();
        var chain = spy(new MockFilterChain());

        filter.doFilter(req, res, chain);

        verifyNoInteractions(jwtService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void semAuthorizationHeader_deveSeguirSemAutenticar() throws Exception {
        var req = new MockHttpServletRequest("GET", "/api/qualquer");
        var res = new MockHttpServletResponse();
        var chain = spy(new MockFilterChain());

        filter.doFilter(req, res, chain);

        verifyNoInteractions(jwtService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void authorizationNaoBearer_deveSeguirSemAutenticar() throws Exception {
        var req = new MockHttpServletRequest("GET", "/api/qualquer");
        req.addHeader("Authorization", "Token abc");
        var res = new MockHttpServletResponse();
        var chain = spy(new MockFilterChain());

        filter.doFilter(req, res, chain);

        verifyNoInteractions(jwtService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenComLoginNull_naoAutentica() throws Exception {
        var req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "Bearer tok123");
        var res = new MockHttpServletResponse();
        var chain = spy(new MockFilterChain());

        when(jwtService.extrairLogin("tok123")).thenReturn(null);

        filter.doFilter(req, res, chain);

        verify(jwtService).extrairLogin("tok123");
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenInvalido_naoAutentica() throws Exception {
        var req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "Bearer invalido");
        var res = new MockHttpServletResponse();
        var chain = spy(new MockFilterChain());

        when(jwtService.extrairLogin("invalido")).thenReturn("erika");
        when(jwtService.isTokenValid("invalido", "erika")).thenReturn(false);

        filter.doFilter(req, res, chain);

        verify(jwtService).extrairLogin("invalido");
        verify(jwtService).isTokenValid("invalido", "erika");
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenValido_autenticaEMapeiaRoles() throws Exception {
        var req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "Bearer ok.token");
        var res = new MockHttpServletResponse();
        var chain = spy(new MockFilterChain());

        when(jwtService.extrairLogin("ok.token")).thenReturn("erika");
        when(jwtService.isTokenValid("ok.token", "erika")).thenReturn(true);
        when(jwtService.extrairRoles("ok.token"))
                .thenReturn(List.of("ADMIN", "ROLE_MANAGER", "USER", ""));

        filter.doFilter(req, res, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("erika", auth.getPrincipal());
        assertTrue(auth.isAuthenticated());

        var authorities = auth.getAuthorities();
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertFalse(authorities.stream().anyMatch(a -> a.getAuthority().isBlank()));

        verify(jwtService).extrairLogin("ok.token");
        verify(jwtService).isTokenValid("ok.token", "erika");
        verify(jwtService).extrairRoles("ok.token");
    }

    @Test
    void excecaoDuranteProcessamento_ehCapturada_eFluxoSegue() throws Exception {
        var req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "Bearer boom");
        var res = new MockHttpServletResponse();
        var chain = mock(FilterChain.class);

        when(jwtService.extrairLogin("boom")).thenThrow(new RuntimeException("qualquer erro"));

        assertDoesNotThrow(() -> filter.doFilter(req, res, chain));
        verify(chain).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
