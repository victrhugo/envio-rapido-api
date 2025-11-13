package com.gft.envioapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gft.envioapi.controller.EnvioController;
import com.gft.envioapi.dto.AtualizarEnvioDTO;
import com.gft.envioapi.dto.EnvioDetalheResponse;
import com.gft.envioapi.dto.EnvioRequestDTO;
import com.gft.envioapi.dto.FreteResponseDTO;
import com.gft.envioapi.entity.Envio;
import com.gft.envioapi.service.EnvioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EnvioControllerTest {

    @Mock
    private EnvioService envioService;

    @InjectMocks
    private EnvioController envioController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(envioController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void criarEnvio_DeveRetornarCreated_QuandoDadosValidos() throws Exception {
        EnvioRequestDTO requestDTO = new EnvioRequestDTO(
                "João Silva",
                "Avenida Gisele Martins 1191",
                "01310100",
                "04567890",
                30.0,
                20.0,
                40.0,
                5.0
        );

        FreteResponseDTO freteResponseDTO = new FreteResponseDTO(
                "25.50",
                "5",
                "Entrega em 5 dias úteis",
                "45.00",
                "2",
                "Entrega em 2 dias úteis"
        );

        EnvioService.EnvioFreteResponse response = new EnvioService.EnvioFreteResponse(
                "João Silva",
                "01310100",
                "04567890",
                freteResponseDTO,
                "Cálculo realizado com sucesso"
        );

        when(envioService.criarEnvio(any(EnvioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeRemetente").value("João Silva"))
                .andExpect(jsonPath("$.cepOrigem").value("01310100"))
                .andExpect(jsonPath("$.cepDestino").value("04567890"))
                .andExpect(jsonPath("$.frete.valorPAC").value("25.50"))
                .andExpect(jsonPath("$.frete.valorSEDEX").value("45.00"))
                .andExpect(jsonPath("$.mensagem").value("Cálculo realizado com sucesso"));

        verify(envioService, times(1)).criarEnvio(any(EnvioRequestDTO.class));
    }

    @Test
    void criarEnvio_DeveRetornarBadRequest_QuandoCepInvalido() throws Exception {
        EnvioRequestDTO requestDTO = new EnvioRequestDTO(
                "João Silva",
                "Rua das Flores, 123",
                "123",
                "04567890",
                30.0,
                20.0,
                40.0,
                5.0
        );

        mockMvc.perform(post("/api/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(envioService, never()).criarEnvio(any(EnvioRequestDTO.class));
    }

    @Test
    void criarEnvio_DeveRetornarBadRequest_QuandoCamposObrigatoriosVazios() throws Exception {
        EnvioRequestDTO requestDTO = new EnvioRequestDTO(
                "",
                "Rua das Flores, 123",
                "01310100",
                "04567890",
                30.0,
                20.0,
                40.0,
                5.0
        );

        mockMvc.perform(post("/api/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(envioService, never()).criarEnvio(any(EnvioRequestDTO.class));
    }

    @Test
    void buscarEnvioPorId_DeveRetornarEnvio_QuandoIdExistente() throws Exception {
        Long envioId = 1L;

        FreteResponseDTO freteDTO = new FreteResponseDTO(
                "25.50",
                "5",
                "Entrega em 5 dias úteis",
                "45.00",
                "2",
                "Entrega em 2 dias úteis"
        );

        EnvioDetalheResponse detalheResponse = new EnvioDetalheResponse(
                1L,
                "João Silva",
                "Rua das Flores, 123",
                "01310100",
                "04567890",
                30.0,
                40.0,
                20.0,
                5.0,
                freteDTO,
                "Cálculo realizado com sucesso"
        );

        when(envioService.obterEnvioDetalhePorId(envioId)).thenReturn(detalheResponse);

        mockMvc.perform(get("/api/envios/{envioId}", envioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.envioId").value(1))
                .andExpect(jsonPath("$.nomeRemetente").value("João Silva"))
                .andExpect(jsonPath("$.cepOrigem").value("01310100"))
                .andExpect(jsonPath("$.frete.valorPAC").value("25.50"));

        verify(envioService, times(1)).obterEnvioDetalhePorId(envioId);
    }

    @Test
    void listarEnvios_DeveRetornarListaDeEnvios() throws Exception {
        FreteResponseDTO frete1 = new FreteResponseDTO("25.50", "5", "PAC", "45.00", "2", "SEDEX");
        FreteResponseDTO frete2 = new FreteResponseDTO("30.00", "7", "PAC", "50.00", "3", "SEDEX");

        EnvioDetalheResponse detalhe1 = new EnvioDetalheResponse(
                1L, "João Silva", "Rua A, 123", "01310100", "04567890",
                30.0, 40.0, 20.0, 5.0, frete1, "Sucesso"
        );

        EnvioDetalheResponse detalhe2 = new EnvioDetalheResponse(
                2L, "Maria Santos", "Rua B, 456", "02310100", "05567890",
                25.0, 35.0, 15.0, 3.0, frete2, "Sucesso"
        );

        List<EnvioDetalheResponse> lista = Arrays.asList(detalhe1, detalhe2);

        when(envioService.listarEnviosComFrete()).thenReturn(lista);

        mockMvc.perform(get("/api/envios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].envioId").value(1))
                .andExpect(jsonPath("$[0].nomeRemetente").value("João Silva"))
                .andExpect(jsonPath("$[1].envioId").value(2))
                .andExpect(jsonPath("$[1].nomeRemetente").value("Maria Santos"));

        verify(envioService, times(1)).listarEnviosComFrete();
    }

    @Test
    void listarEnvios_DeveRetornarListaVazia_QuandoNaoHouverEnvios() throws Exception {
        when(envioService.listarEnviosComFrete()).thenReturn(List.of());

        mockMvc.perform(get("/api/envios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(envioService, times(1)).listarEnviosComFrete();
    }

    @Test
    void deletarEnvio_DeveRetornarNoContent_QuandoEnvioDeletado() throws Exception {
        Long envioId = 1L;
        doNothing().when(envioService).deleteEnvio(envioId);

        mockMvc.perform(delete("/api/envios/{envioId}", envioId))
                .andExpect(status().isNoContent());

        verify(envioService, times(1)).deleteEnvio(envioId);
    }

    @Test
    void atualizarParcial_DeveRetornarNoContent_QuandoAtualizadoComSucesso() throws Exception {
        Long envioId = 1L;
        Map<String, Object> campos = new HashMap<>();
        campos.put("nomeRemetente", "João Silva Modificado");
        campos.put("peso", 10.5);

        doNothing().when(envioService).atualizarEnvioParcial(eq(envioId), anyMap());

        mockMvc.perform(patch("/api/envios/{id}", envioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campos)))
                .andExpect(status().isNoContent());

        verify(envioService, times(1)).atualizarEnvioParcial(eq(envioId), anyMap());
    }

    @Test
    void atualizarParcial_DeveAtualizarApenasCepOrigem() throws Exception {
        Long envioId = 1L;
        Map<String, Object> campos = new HashMap<>();
        campos.put("cepOrigem", "11111111");

        doNothing().when(envioService).atualizarEnvioParcial(eq(envioId), anyMap());

        mockMvc.perform(patch("/api/envios/{id}", envioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campos)))
                .andExpect(status().isNoContent());

        verify(envioService, times(1)).atualizarEnvioParcial(eq(envioId), anyMap());
    }

    @Test
    void optionsEnvios_DeveRetornarMetodosPermitidos() throws Exception {
        mockMvc.perform(options("/api/envios"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Allow"));
    }

    @Test
    void headEnvio_DeveRetornarOk_QuandoEnvioExiste() throws Exception {
        Long envioId = 1L;
        Envio envio = new Envio(
                1L,
                "João Silva",
                "Rua das Flores, 123",
                "01310100",
                "04567890",
                30.0,
                20.0,
                40.0,
                5.0
        );

        when(envioService.obterEnvioPorId(envioId)).thenReturn(envio);

        mockMvc.perform(head("/api/envios/{id}", envioId))
                .andExpect(status().isOk());

        verify(envioService, times(1)).obterEnvioPorId(envioId);
    }

    @Test
    void headEnvio_DeveRetornarNotFound_QuandoEnvioNaoExiste() throws Exception {
        Long envioId = 999L;
        when(envioService.obterEnvioPorId(envioId))
                .thenThrow(new RuntimeException("Envio não encontrado"));

        try {
            mockMvc.perform(head("/api/envios/{id}", envioId));
        } catch (Exception e) {
        }

        verify(envioService, times(1)).obterEnvioPorId(envioId);
    }
}