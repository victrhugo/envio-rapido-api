package com.gft.envioapi;

import com.gft.envioapi.exception.GlobalExceptionHandler;
import com.gft.envioapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler h = new GlobalExceptionHandler();

    @Test
    void handleNotFound() {
        var resp = h.handleNotFound(new ResourceNotFoundException("x"));
        assertEquals(404, resp.getStatusCode().value());
        assertEquals("x", resp.getBody());
    }

    @Test
    void handleValidationErrors() {
        var resp = h.handleUnreadable(new HttpMessageNotReadableException("bad"));
        assertEquals(400, resp.getStatusCode().value());
        assertTrue(resp.getBody().containsKey("erro"));
    }

    @Test
    void handleDataIntegrity() {
        var resp = h.handleDataIntegrity(new DataIntegrityViolationException("dup"));
        assertEquals(400, resp.getStatusCode().value());
        assertTrue(resp.getBody().containsKey("erro"));
    }

    @Test
    void handleUnsupportedMediaType() {
        var resp = h.handleUnsupportedMediaType();
        assertEquals(415, resp.getStatusCode().value());
    }

    @Test
    void handleNotAcceptable() {
        var resp = h.handleNotAcceptable();
        assertEquals(406, resp.getStatusCode().value());
    }

    @Test
    void handleResponseStatus() {
        var he = new GlobalExceptionHandler();
        var resp = he.handleResponseStatus(new ResponseStatusException(HttpStatus.BAD_REQUEST, "msg"));
        assertEquals(400, resp.getStatusCode().value());
        assertEquals("msg", resp.getBody());
    }

    @Test
    void handleRuntime() {
        var he = new GlobalExceptionHandler();
        var resp = he.handleRuntime(new RuntimeException("boom"));
        assertEquals(500, resp.getStatusCode().value());
        assertEquals("boom", resp.getBody());
    }
}
