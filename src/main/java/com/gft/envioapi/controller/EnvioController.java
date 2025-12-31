package com.gft.envioapi.controller;

import com.gft.envioapi.dto.EnvioDetalheResponse;
import com.gft.envioapi.dto.EnvioRequestDTO;
import com.gft.envioapi.service.EnvioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    @PostMapping("/envios")
    public ResponseEntity<EnvioService.EnvioFreteResponse> criarEnvio(
            @RequestBody @Valid EnvioRequestDTO dto) {
        var response = envioService.criarEnvio(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/envios/{envioId}")
    public ResponseEntity<EnvioDetalheResponse> buscarEnvioPorId(@PathVariable Long envioId) {
        var detalhe = envioService.obterEnvioDetalhePorId(envioId);
        return ResponseEntity.ok(detalhe);
    }

    @GetMapping("/envios")
    public ResponseEntity<List<EnvioDetalheResponse>> listarEnvios() {
        var lista = envioService.listarEnviosComFrete();
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/envios/{envioId}")
    public ResponseEntity<Void> deletarEnvio(@PathVariable Long envioId) {
        envioService.deleteEnvio(envioId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/envios/{id}")
    public ResponseEntity<Void> atualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos) {
        envioService.atualizarEnvioParcial(id, campos);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/envios", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> optionsEnvios() {
        return ResponseEntity
                .ok()
                .allow(
                        HttpMethod.GET,
                        HttpMethod.POST,
                        HttpMethod.PUT,
                        HttpMethod.PATCH,
                        HttpMethod.DELETE,
                        HttpMethod.OPTIONS,
                        HttpMethod.HEAD
                )
                .build();
    }

    @RequestMapping(value = "/envios/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> headEnvio(@PathVariable Long id) {
        envioService.obterEnvioPorId(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/envios/recalcular-fretes")
    public ResponseEntity<String> recalcularTodosFretes() {
        envioService.recalcularTodosFretes();
        return ResponseEntity.ok("Fretes recalculados com sucesso!");
    }
}