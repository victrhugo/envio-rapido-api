package com.gft.envioapi.client;

import com.gft.envioapi.dto.ViaCepDTO;
import com.gft.envioapi.dto.ViaCepResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "viacep", url = "https://viacep.com.br/ws")
public interface ViaCepClient {
    @GetMapping("{cep}/json")
    ViaCepDTO buscar (@PathVariable("cep") String cep);
}
