package com.gft.envioapi.service;

import com.gft.envioapi.client.FreteClient;
import com.gft.envioapi.dto.FreteInternoDTO;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
public class FreteCalculatorService {

    private static final Logger logger = LoggerFactory.getLogger(FreteCalculatorService.class);
    private final FreteClient freteClient;
    private final String meServices;

    public FreteCalculatorService(FreteClient freteClient,
                                   @Value("${melhorenvio.services:}") String meServices) {
        this.freteClient = freteClient;
        this.meServices = meServices;
    }

    public FreteInternoDTO calcularFrete(String cepOrigem, String cepDestino,
                                         double altura, double largura,
                                         double comprimento, double peso) {

        var request = construirRequest(cepOrigem, cepDestino, altura, largura, comprimento, peso);
        var quotes = buscarCotacoes(request);
        var cotacoesValidas = filtrarCotacoesValidas(quotes);

        if (cotacoesValidas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nenhuma transportadora disponível para este trecho. " +
                            "Verifique CEPs e dimensões do pacote.");
        }

        return construirResposta(cotacoesValidas);
    }

    private FreteClient.CalculateRequest construirRequest(String cepOrigem, String cepDestino,
                                                          double altura, double largura,
                                                          double comprimento, double peso) {
        return new FreteClient.CalculateRequest(
                new FreteClient.FromAddress(cepOrigem),
                new FreteClient.ToAddress(cepDestino),
                new FreteClient.PackageInfo(altura, largura, comprimento, peso),
                (meServices == null || meServices.isBlank()) ? null : meServices
        );
    }

    private List<FreteClient.QuoteResponse> buscarCotacoes(FreteClient.CalculateRequest request) {
        try {
            return freteClient.calcular(request);
        } catch (FeignException e) {
            tratarErroMelhorEnvio(e);
            return List.of(); // Nunca será alcançado, mas necessário para compilação
        }
    }

    private void tratarErroMelhorEnvio(FeignException e) {
        String body = extrairBody(e);
        logger.error("Erro ao calcular frete no Melhor Envio - Status: {} | Body: {}", e.status(), body);

        if (e.status() == 401 || e.status() == 403) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Token inválido/escopo/ambiente. Status=" + e.status());
        }
        if (e.status() == 422) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Payload inválido: " + body);
        }
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "Falha ao cotar frete: status=" + e.status());
    }

    private String extrairBody(FeignException e) {
        try {
            return e.contentUTF8();
        } catch (Exception ignore) {
            return "";
        }
    }

    private List<FreteClient.QuoteResponse> filtrarCotacoesValidas(List<FreteClient.QuoteResponse> quotes) {
        return quotes.stream()
                .filter(FreteClient.QuoteResponse::isValid)
                .toList();
    }

    private FreteInternoDTO construirResposta(List<FreteClient.QuoteResponse> cotacoes) {
        var cotacoesOrdenadas = cotacoes.stream()
                .sorted(Comparator.comparing(q -> new BigDecimal(q.getPrecoEfetivo())))
                .toList();

        var pac = buscarServico(cotacoesOrdenadas, "PAC");
        var sedex = buscarServico(cotacoesOrdenadas, "SEDEX");

        return new FreteInternoDTO(
                pac.orElse(FreteInternoDTO.ServicoFreteDTO.indisponivel(
                        "PAC não disponível para esta rota")),
                sedex.orElse(FreteInternoDTO.ServicoFreteDTO.indisponivel(
                        "SEDEX não disponível para esta rota")),
                "Cálculo de frete realizado com sucesso"
        );
    }

    private Optional<FreteInternoDTO.ServicoFreteDTO> buscarServico(
            List<FreteClient.QuoteResponse> cotacoes, String nomeServico) {

        return cotacoes.stream()
                .filter(q -> q.name() != null && q.name().equalsIgnoreCase(nomeServico))
                .findFirst()
                .map(q -> {
                    String valor = q.getPrecoEfetivo();
                    Integer prazoInt = q.getPrazoEfetivo();
                    String prazo = (prazoInt != null) ? String.valueOf(prazoInt) : "0";
                    
                    logger.debug("Serviço {} - Valor: {}, Prazo: {}", nomeServico, valor, prazo);
                    
                    return FreteInternoDTO.ServicoFreteDTO.disponivel(valor, prazo);
                });
    }
}