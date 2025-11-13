package com.gft.envioapi.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record EnvioRequestDTO(

        @NotBlank(message = "O nome é obrigatório.")
        String nomeRemetente,

        @NotBlank(message = "O endereço é obrigatório.")
        String endereco,

        @Pattern(regexp = "\\d{8}", message = "cepOrigem deve ter 8 dígitos")
        @NotBlank(message = "O cep de origem é obrigatório.")
        String cepOrigem,

        @Pattern(regexp = "\\d{8}", message = "cepOrigem deve ter 8 dígitos")
        @NotBlank(message = "O cep de destino é obrigatório.")
        String cepDestino,

        @Positive @NotNull(message = "A largura da caixa é obrigatório.")
        double larguraCaixa,

        @Positive @NotNull(message = "A altura da caixa é obrigatório.")
        double alturaCaixa,

        @Positive @NotNull(message = "O comprimento da caixa é obrigatório.")
        double ComprimentoCaixa,

        @Positive @NotNull(message = "O peso da caixa é obrigatório.")
        double peso


) {}
