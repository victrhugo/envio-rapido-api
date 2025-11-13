package com.gft.envioapi.dto;

public record ViaCepDTO(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        Boolean erro
) {}
