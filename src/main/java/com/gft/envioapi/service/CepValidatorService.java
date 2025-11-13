package com.gft.envioapi.service;

import com.gft.envioapi.client.ViaCepClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CepValidatorService {

    @Autowired
    private ViaCepClient cepClient;

    public void validar(String cep) {
        String cepLimpo = limparCep(cep);
        validarFormato(cepLimpo, cep);
        validarExistencia(cepLimpo);
    }

    private String limparCep(String cep) {
        return (cep == null) ? "" : cep.replaceAll("\\D", "");
    }

    private void validarFormato(String cepLimpo, String cepOriginal) {
        if (!cepLimpo.matches("\\d{8}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "CEP inválido (formato esperado: 8 dígitos): " + cepOriginal
            );
        }
    }

    private void validarExistencia(String cepLimpo) {
        try {
            var dto = cepClient.buscar(cepLimpo);
            if (dto == null || Boolean.TRUE.equals(dto.erro())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "CEP não encontrado: " + cepLimpo
                );
            }
        } catch (FeignException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Serviço ViaCEP temporariamente indisponível. Tente novamente."
            );
        }
    }
}