package com.gft.envioapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EnvioDetalheResponse(
        Long envioId,
        String nomeRemetente,
        String endereco,
        String cepOrigem,
        String cepDestino,
        double larguraCaixa,
        double comprimentoCaixa,
        double alturaCaixa,
        double peso,
        FreteResponseDTO frete,
        String mensagemGeral
) {}