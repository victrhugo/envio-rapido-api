package com.gft.envioapi;

import com.gft.envioapi.client.FreteClient;
import com.gft.envioapi.client.FreteClient.*;
import com.gft.envioapi.dto.FreteInternoDTO;
import com.gft.envioapi.service.FreteCalculatorService;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;



@ExtendWith(MockitoExtension.class)
class FreteCalculatorServiceTest {

    @Mock
    private FreteClient freteClient;

    @InjectMocks
    private FreteCalculatorService service;

    @Captor
    private ArgumentCaptor<CalculateRequest> requestCaptor;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "meServices", "1,2");
    }

    @Test
    void calcularFrete_DeveRetornarPacESedex_Disponiveis() {
        var pac   = quote("PAC",   "25.50", null, 5, null, null);
        var sedex = quote("SEDEX", "45.00", null, 2, null, null);

        when(freteClient.calcular(any())).thenReturn(List.of(sedex, pac));

        FreteInternoDTO dto = service.calcularFrete(
                "01310100", "04567890", 30.0, 20.0, 40.0, 5.0
        );

        assertNotNull(dto);
        assertEquals("Cálculo de frete realizado com sucesso", dto.mensagem());

        assertTrue(dto.pac().disponivel());
        assertEquals("25.50", dto.pac().valor());
        assertNotNull(dto.pac().prazo());
        assertNull(dto.pac().observacao());

        assertTrue(dto.sedex().disponivel());
        assertEquals("45.00", dto.sedex().valor());
        assertNotNull(dto.sedex().prazo());
        assertNull(dto.sedex().observacao());

        verify(freteClient).calcular(requestCaptor.capture());
        var req = requestCaptor.getValue();
        assertEquals("01310100", req.from().postalCode());
        assertEquals("04567890", req.to().postalCode());
        assertEquals(30.0, req.packageInfo().height());
        assertEquals(20.0, req.packageInfo().width());
        assertEquals(40.0, req.packageInfo().length());
        assertEquals(5.0,  req.packageInfo().weight());
        assertEquals("1,2", req.services());
        verifyNoMoreInteractions(freteClient);
    }

    @Test
    void calcularFrete_DeveRetornarSedexIndisponivel_QuandoSoPac() {
        var pac = quote("PAC", "30.00", null, 7, null, null);
        when(freteClient.calcular(any())).thenReturn(List.of(pac));

        var dto = service.calcularFrete("01310100", "04567890", 10, 10, 10, 1);

        assertTrue(dto.pac().disponivel());
        assertFalse(dto.sedex().disponivel());
        assertEquals("SEDEX não disponível para esta rota", dto.sedex().observacao());
    }

    @Test
    void calcularFrete_DeveLancarBadRequest_QuandoSemCotacoesValidas() {
        var invalida1 = quote("PAC", null, null, 5, null, null);
        var invalida2 = quote("SEDEX", "", null, 2, null, null);
        var invalida3 = quote("PAC", "10.00", null, 5, null, "erro x");
        when(freteClient.calcular(any())).thenReturn(List.of(invalida1, invalida2, invalida3));

        var ex = assertThrows(ResponseStatusException.class, () ->
                service.calcularFrete("01310100", "04567890", 10, 10, 10, 1)
        );

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Nenhuma transportadora disponível"));
    }

    @Test
    void calcularFrete_DeveLancarUnauthorized_QuandoFeign401_ou_403() {
        var feign401 = feignEx(401, "unauthorized");
        when(freteClient.calcular(any())).thenThrow(feign401);

        var ex1 = assertThrows(ResponseStatusException.class, () ->
                service.calcularFrete("01310100", "04567890", 1, 1, 1, 1)
        );
        assertEquals(401, ex1.getStatusCode().value());
        assertTrue(ex1.getReason().contains("Token inválido"));

        reset(freteClient);
        var feign403 = feignEx(403, "forbidden");
        when(freteClient.calcular(any())).thenThrow(feign403);

        var ex2 = assertThrows(ResponseStatusException.class, () ->
                service.calcularFrete("01310100", "04567890", 1, 1, 1, 1)
        );
        assertEquals(401, ex2.getStatusCode().value());
    }

    @Test
    void calcularFrete_DeveLancarBadRequest_QuandoFeign422() {
        var feign422 = feignEx(422, "{\"error\":\"payload inválido\"}");
        when(freteClient.calcular(any())).thenThrow(feign422);

        var ex = assertThrows(ResponseStatusException.class, () ->
                service.calcularFrete("01310100", "04567890", 1, 1, 1, 1)
        );

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().startsWith("Payload inválido"));
    }

    @Test
    void calcularFrete_DeveLancarBadGateway_QuandoFeign500() {
        var feign500 = feignEx(500, "internal error");
        when(freteClient.calcular(any())).thenThrow(feign500);

        var ex = assertThrows(ResponseStatusException.class, () ->
                service.calcularFrete("01310100", "04567890", 1, 1, 1, 1)
        );

        assertEquals(502, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Falha ao cotar frete"));
    }


    private QuoteResponse quote(
            String name,
            String price,
            String customPrice,
            Integer deliveryTime,
            Integer customDeliveryTime,
            String error
    ) {
        return new QuoteResponse(
                "id-" + name,
                name,
                new Company("Comp"),
                new Service(name),
                price,
                customPrice,
                deliveryTime,
                customDeliveryTime,
                error
        );
    }

    private FeignException feignEx(int status, String body) {
        byte[] bytes = body == null ? new byte[]{} : body.getBytes(StandardCharsets.UTF_8);

        Request request = Request.create(
                Request.HttpMethod.POST,
                "http://example.com/api/v2/me/shipment/calculate",
                Map.of(),
                Request.Body.create(bytes),
                new RequestTemplate()
        );

        feign.Response response = feign.Response.builder()
                .status(status)
                .reason("status-" + status)
                .request(request)
                .headers(Map.of())
                .body(bytes)
                .build();

        return feign.FeignException.errorStatus("melhorEnvio", response);
    }
}
