package com.gft.envioapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FreteResponseDTO(
        @JsonProperty("valorPAC")
        String valorPAC,

        @JsonProperty("prazoPAC")
        String prazoPAC,

        @JsonProperty("mensagemPAC")
        String mensagemPAC,

        @JsonProperty("valorSEDEX")
        String valorSEDEX,

        @JsonProperty("prazoSEDEX")
        String prazoSEDEX,

        @JsonProperty("mensagemSEDEX")
        String mensagemSEDEX
) {}