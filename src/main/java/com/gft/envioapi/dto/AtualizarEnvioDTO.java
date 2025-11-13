package com.gft.envioapi.dto;

import jakarta.validation.constraints.NotBlank;

public record AtualizarEnvioDTO(String nomeRemetente, String endereco, String cepOrigem, String cepDestino, int larguraCaixa,
                                int alturaCaixa, int comprimentoCaixa, float peso) {
}
