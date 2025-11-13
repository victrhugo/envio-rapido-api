package com.gft.envioapi;

import com.gft.envioapi.client.ViaCepClient;
import com.gft.envioapi.dto.ViaCepDTO;
import com.gft.envioapi.service.CepValidatorService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CepValidatorTest {

    @Mock
    private ViaCepClient viaCepClient;

    @InjectMocks
    private CepValidatorService cepValidatorService;

    @Test
    void validar_DevePassar_QuandoCepValidoEExiste() {
        String cepEntrada = "01310-100";
        String cepLimpo   = "01310100";

        ViaCepDTO dto = new ViaCepDTO(
                cepLimpo,
                null,
                null,
                null,
                null,
                false
        );
        when(viaCepClient.buscar(cepLimpo)).thenReturn(dto);

        assertDoesNotThrow(() -> cepValidatorService.validar(cepEntrada));
        verify(viaCepClient).buscar(cepLimpo);
        verifyNoMoreInteractions(viaCepClient);
    }

    @Test
    void validar_DeveLancarBadRequest_QuandoFormatoInvalido() {
        String cepInvalido = "123";

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> cepValidatorService.validar(cepInvalido));

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("CEP inválido"));
        verifyNoInteractions(viaCepClient);
    }

    @Test
    void validar_DeveLancarBadRequest_QuandoCepNaoEncontrado() {
        String cep = "99999999";
        ViaCepDTO dto = new ViaCepDTO(
                cep,
                null, null, null, null,
                true
        );
        when(viaCepClient.buscar(cep)).thenReturn(dto);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> cepValidatorService.validar(cep));

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("CEP não encontrado"));
        verify(viaCepClient).buscar(cep);
    }

    @Test
    void validar_DeveLancarBadGateway_QuandoViaCepIndisponivel() {
        String cep = "01310100";
        FeignException feignEx = mock(FeignException.class);
        when(viaCepClient.buscar(cep)).thenThrow(feignEx);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> cepValidatorService.validar(cep));

        assertEquals(502, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("ViaCEP temporariamente indisponível"));
        verify(viaCepClient).buscar(cep);
    }

    @Test
    void validar_DeveLancarBadRequest_QuandoViaCepRetornaNull() {
        String cep = "01310100";
        when(viaCepClient.buscar(cep)).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> cepValidatorService.validar(cep));

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("CEP não encontrado"));
        verify(viaCepClient).buscar(cep);
    }
}
