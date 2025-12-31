package com.gft.envioapi.service;

import com.gft.envioapi.dto.*;
import com.gft.envioapi.entity.Envio;
import com.gft.envioapi.entity.Frete;
import com.gft.envioapi.exception.ResourceNotFoundException;
import com.gft.envioapi.repository.EnvioRepository;
import com.gft.envioapi.repository.FreteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EnvioService {

    private static final Logger logger = LoggerFactory.getLogger(EnvioService.class);
    
    private final EnvioRepository envioRepository;
    private final CepValidatorService cepValidator;
    private final FreteCalculatorService freteCalculator;
    private final FreteRepository freteRepository;

    public EnvioService(EnvioRepository envioRepository,
                        CepValidatorService cepValidator,
                        FreteCalculatorService freteCalculator,
                        FreteRepository freteRepository) {
        this.envioRepository = envioRepository;
        this.cepValidator = cepValidator;
        this.freteCalculator = freteCalculator;
        this.freteRepository = freteRepository;
    }

    @Transactional
    public EnvioFreteResponse criarEnvio(EnvioRequestDTO dto) {
        validarCeps(dto.cepOrigem(), dto.cepDestino());

        var envio = construirEnvio(dto);
        envioRepository.save(envio);

        var freteInterno = freteCalculator.calcularFrete(
                dto.cepOrigem(),
                dto.cepDestino(),
                dto.alturaCaixa(),
                dto.larguraCaixa(),
                dto.ComprimentoCaixa(),
                dto.peso()
        );

        var frete = construirFrete(envio, freteInterno);
        freteRepository.save(frete);

        var freteResponse = new FreteResponseDTO(
                freteInterno.pac().valor(),
                freteInterno.pac().prazo(),
                freteInterno.pac().observacao(),
                freteInterno.sedex().valor(),
                freteInterno.sedex().prazo(),
                freteInterno.sedex().observacao()
        );

        return new EnvioFreteResponse(
                envio.getNomeRemetente(),
                envio.getCepOrigem(),
                envio.getCepDestino(),
                freteResponse,
                freteInterno.mensagem()
        );
    }

    private Frete construirFrete(Envio envio, FreteInternoDTO freteInterno) {
        var frete = new Frete();
        frete.setEnvio(envio);

        var pac = freteInterno.pac();
        frete.setPacDisponivel(pac.disponivel());
        frete.setPacValor(pac.valor());
        frete.setPacPrazo(pac.prazo());
        frete.setPacMensagem(pac.observacao());

        var sedex = freteInterno.sedex();
        frete.setSedexDisponivel(sedex.disponivel());
        frete.setSedexValor(sedex.valor());
        frete.setSedexPrazo(sedex.prazo());
        frete.setSedexMensagem(sedex.observacao());

        frete.setMensagemGeral(freteInterno.mensagem());

        return frete;
    }

    public List<EnvioDetalheResponse> listarEnviosComFrete() {
        var envios = envioRepository.findAll();
        return envios.stream()
                .map(this::toDetalheResponse)
                .collect(Collectors.toList());
    }

    public Envio obterEnvioPorId(Long envioId) {
        return envioRepository.findById(envioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Envio não encontrado com ID: " + envioId));
    }

    public EnvioDetalheResponse obterEnvioDetalhePorId(Long envioId) {
        var envio = obterEnvioPorId(envioId);
        return toDetalheResponse(envio);
    }

    @Transactional
    public void atualizarEnvio(Long envioId, AtualizarEnvioDTO dto) {
        var envio = obterEnvioPorId(envioId);

        validarCeps(dto.cepOrigem(), dto.cepDestino());
        atualizarDadosEnvio(envio, dto);
        envioRepository.save(envio);

        recalcularEAtualizarFrete(envio);
    }

    @Transactional
    public void atualizarEnvioParcial(Long envioId, Map<String, Object> campos) {
        var envio = obterEnvioPorId(envioId);

        validarCepsParcial(campos);
        aplicarCamposParciais(envio, campos);
        envioRepository.save(envio);

        recalcularEAtualizarFrete(envio);
    }

    @Transactional
    public void deleteEnvio(Long envioId) {
        var envio = obterEnvioPorId(envioId);
        freteRepository.findByEnvioEnvioId(envioId).ifPresent(freteRepository::delete);
        envioRepository.delete(envio);
    }

    @Transactional
    public void recalcularTodosFretes() {
        var envios = envioRepository.findAll();
        for (var envio : envios) {
            try {
                recalcularEAtualizarFrete(envio);
            } catch (Exception e) {
                logger.error("Erro ao recalcular frete para envio {}: {}", envio.getEnvioId(), e.getMessage());
            }
        }
    }

    private void validarCeps(String cepOrigem, String cepDestino) {
        cepValidator.validar(cepOrigem);
        cepValidator.validar(cepDestino);
    }

    private void validarCepsParcial(Map<String, Object> campos) {
        if (campos.containsKey("cepOrigem")) {
            cepValidator.validar(String.valueOf(campos.get("cepOrigem")));
        }
        if (campos.containsKey("cepDestino")) {
            cepValidator.validar(String.valueOf(campos.get("cepDestino")));
        }
    }

    private Envio construirEnvio(EnvioRequestDTO dto) {
        return new Envio(
                null,
                dto.nomeRemetente(),
                dto.endereco(),
                dto.cepOrigem(),
                dto.cepDestino(),
                dto.larguraCaixa(),
                dto.ComprimentoCaixa(),
                dto.alturaCaixa(),
                dto.peso()
        );
    }

    private void atualizarDadosEnvio(Envio envio, AtualizarEnvioDTO dto) {
        envio.setNomeRemetente(dto.nomeRemetente());
        envio.setCepOrigem(dto.cepOrigem());
        envio.setCepDestino(dto.cepDestino());
        envio.setLarguraCaixa(dto.larguraCaixa());
        envio.setAlturaCaixa(dto.alturaCaixa());
        envio.setComprimentoCaixa(dto.comprimentoCaixa());
    }

    private void aplicarCamposParciais(Envio envio, Map<String, Object> campos) {
        BeanWrapper wrapper = new BeanWrapperImpl(envio);
        campos.forEach((campo, valor) -> {
            if (wrapper.isWritableProperty(campo)) {
                wrapper.setPropertyValue(campo, valor);
            }
        });
    }

    public record EnvioFreteResponse(
            String nomeRemetente,
            String cepOrigem,
            String cepDestino,
            FreteResponseDTO frete,
            String mensagem
    ) {
    }

    private EnvioDetalheResponse toDetalheResponse(Envio envio) {
        var freteOpt = freteRepository.findByEnvioEnvioId(envio.getEnvioId());

        FreteResponseDTO freteDto = null;
        String mensagemGeral = null;

        if (freteOpt.isPresent()) {
            var f = freteOpt.get();
            freteDto = new FreteResponseDTO(
                    f.getPacValor(),
                    f.getPacPrazo(),
                    f.getPacMensagem(),
                    f.getSedexValor(),
                    f.getSedexPrazo(),
                    f.getSedexMensagem()
            );
            mensagemGeral = f.getMensagemGeral();
        }

        return new EnvioDetalheResponse(
                envio.getEnvioId(),
                envio.getNomeRemetente(),
                envio.getEndereco(),
                envio.getCepOrigem(),
                envio.getCepDestino(),
                envio.getLarguraCaixa(),
                envio.getComprimentoCaixa(),
                envio.getAlturaCaixa(),
                envio.getPeso(),
                freteDto,
                mensagemGeral
        );
    }

    private void recalcularEAtualizarFrete(Envio envio) {
        var freteInterno = freteCalculator.calcularFrete(
                envio.getCepOrigem(),
                envio.getCepDestino(),
                envio.getAlturaCaixa(),
                envio.getLarguraCaixa(),
                envio.getComprimentoCaixa(),
                envio.getPeso()
        );

        var frete = freteRepository.findByEnvioEnvioId(envio.getEnvioId())
                .orElseThrow(() -> new ResourceNotFoundException("Frete não encontrado para o envio " + envio.getEnvioId()));

        var pac = freteInterno.pac();
        frete.setPacDisponivel(pac.disponivel());
        frete.setPacValor(pac.valor());
        frete.setPacPrazo(pac.prazo());
        frete.setPacMensagem(pac.observacao());

        var sedex = freteInterno.sedex();
        frete.setSedexDisponivel(sedex.disponivel());
        frete.setSedexValor(sedex.valor());
        frete.setSedexPrazo(sedex.prazo());
        frete.setSedexMensagem(sedex.observacao());

        frete.setMensagemGeral(freteInterno.mensagem());

        freteRepository.save(frete);
    }
}

