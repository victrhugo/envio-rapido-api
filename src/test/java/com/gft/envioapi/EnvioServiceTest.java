package com.gft.envioapi;

import com.gft.envioapi.dto.*;
import com.gft.envioapi.entity.Envio;
import com.gft.envioapi.entity.Frete;
import com.gft.envioapi.exception.ResourceNotFoundException;
import com.gft.envioapi.repository.EnvioRepository;
import com.gft.envioapi.repository.FreteRepository;
import com.gft.envioapi.service.CepValidatorService;
import com.gft.envioapi.service.EnvioService;
import com.gft.envioapi.service.FreteCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EnvioService")
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private CepValidatorService cepValidator;

    @Mock
    private FreteCalculatorService freteCalculator;

    @Mock
    private FreteRepository freteRepository;

    @InjectMocks
    private EnvioService envioService;

    private EnvioRequestDTO envioRequestDTO;
    private Envio envio;
    private Frete frete;
    private FreteInternoDTO freteInternoDTO;

    @BeforeEach
    void setUp() {

        envioRequestDTO = new EnvioRequestDTO(
                "João Silva",
                "Rua Teste, 123",
                "01310100",
                "04567890",
                10.0,
                20.0,
                30.0,
                5.0
        );

        envio = new Envio(
                1L,
                "João Silva",
                "Rua Teste, 123",
                "01310100",
                "04567890",
                10.0,
                20.0,
                30.0,
                5.0
        );

        var pacDTO = FreteInternoDTO.ServicoFreteDTO.disponivel("25.50", "5");
        var sedexDTO = FreteInternoDTO.ServicoFreteDTO.disponivel("45.00", "2");

        freteInternoDTO = new FreteInternoDTO(
                pacDTO,
                sedexDTO,
                "Cálculo realizado com sucesso"
        );

        frete = new Frete();
        frete.setFreteId(1L);
        frete.setEnvio(envio);
        frete.setPacDisponivel(true);
        frete.setPacValor("25.50");
        frete.setPacPrazo("5");
        frete.setPacMensagem("Entrega em até 5 dias úteis");
        frete.setSedexDisponivel(true);
        frete.setSedexValor("45.00");
        frete.setSedexPrazo("2");
        frete.setSedexMensagem("Entrega em até 2 dias úteis");
        frete.setMensagemGeral("Cálculo realizado com sucesso");
    }

    @Test
    @DisplayName("Deve criar envio com sucesso")
    void deveCriarEnvioComSucesso() {
        doNothing().when(cepValidator).validar(anyString());
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        when(freteCalculator.calcularFrete(anyString(), anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(freteInternoDTO);
        when(freteRepository.save(any(Frete.class))).thenReturn(frete);

        var result = envioService.criarEnvio(envioRequestDTO);

        assertNotNull(result);
        assertEquals("João Silva", result.nomeRemetente());
        assertEquals("01310100", result.cepOrigem());
        assertEquals("04567890", result.cepDestino());
        assertNotNull(result.frete());
        assertEquals("25.50", result.frete().valorPAC());
        assertEquals("45.00", result.frete().valorSEDEX());
        assertEquals("5", result.frete().prazoPAC());
        assertEquals("2", result.frete().prazoSEDEX());
        assertEquals("Cálculo realizado com sucesso", result.mensagem());

        verify(cepValidator, times(2)).validar(anyString());
        verify(envioRepository, times(1)).save(any(Envio.class));
        verify(freteCalculator, times(1)).calcularFrete(anyString(), anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
        verify(freteRepository, times(1)).save(any(Frete.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar envio com CEP inválido")
    void deveLancarExcecaoAoCriarEnvioComCepInvalido() {

        doThrow(new IllegalArgumentException("CEP inválido"))
                .when(cepValidator).validar(anyString());

        assertThrows(IllegalArgumentException.class, () -> {
            envioService.criarEnvio(envioRequestDTO);
        });

        verify(cepValidator, times(1)).validar(anyString());
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    @DisplayName("Deve criar envio com frete PAC indisponível")
    void deveCriarEnvioComFretePacIndisponivel() {

        var pacIndisponivel = FreteInternoDTO.ServicoFreteDTO.indisponivel("Serviço temporariamente indisponível");
        var sedexDisponivel = FreteInternoDTO.ServicoFreteDTO.disponivel("45.00", "2");
        var freteComPacIndisponivel = new FreteInternoDTO(pacIndisponivel, sedexDisponivel, "PAC indisponível");

        doNothing().when(cepValidator).validar(anyString());
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        when(freteCalculator.calcularFrete(anyString(), anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(freteComPacIndisponivel);

        when(freteRepository.save(any(Frete.class))).thenReturn(frete);

        var result = envioService.criarEnvio(envioRequestDTO);

        assertNotNull(result);
        assertEquals("PAC indisponível", result.mensagem());
        verify(freteRepository, times(1)).save(any(Frete.class));
    }

    @Test
    @DisplayName("Deve listar todos os envios com frete")
    void deveListarTodosEnviosComFrete() {

        List<Envio> envios = Arrays.asList(envio);
        when(envioRepository.findAll()).thenReturn(envios);
        when(freteRepository.findByEnvioEnvioId(1L)).thenReturn(Optional.of(frete));

        var result = envioService.listarEnviosComFrete();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("João Silva", result.get(0).nomeRemetente());
        assertEquals("01310100", result.get(0).cepOrigem());
        assertNotNull(result.get(0).frete());

        verify(envioRepository, times(1)).findAll();
        verify(freteRepository, times(1)).findByEnvioEnvioId(1L);
    }

    @Test
    @DisplayName("Deve listar envios sem frete associado")
    void deveListarEnviosSemFreteAssociado() {

        List<Envio> envios = Arrays.asList(envio);
        when(envioRepository.findAll()).thenReturn(envios);
        when(freteRepository.findByEnvioEnvioId(1L)).thenReturn(Optional.empty());


        var result = envioService.listarEnviosComFrete();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).frete());
        assertNull(result.get(0).mensagemGeral());

        verify(envioRepository, times(1)).findAll();
        verify(freteRepository, times(1)).findByEnvioEnvioId(1L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há envios")
    void deveRetornarListaVaziaQuandoNaoHaEnvios() {

        when(envioRepository.findAll()).thenReturn(Collections.emptyList());

        var result = envioService.listarEnviosComFrete();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(envioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve obter envio por ID")
    void deveObterEnvioPorId() {

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));

        var result = envioService.obterEnvioPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getEnvioId());
        assertEquals("João Silva", result.getNomeRemetente());
        assertEquals("01310100", result.getCepOrigem());

        verify(envioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar envio inexistente")
    void deveLancarExcecaoAoBuscarEnvioInexistente() {

        when(envioRepository.findById(999L)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> {
            envioService.obterEnvioPorId(999L);
        });

        assertEquals("Envio não encontrado com ID: 999", exception.getMessage());
        verify(envioRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve obter detalhes do envio por ID")
    void deveObterEnvioDetalhePorId() {

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(freteRepository.findByEnvioEnvioId(1L)).thenReturn(Optional.of(frete));

        var result = envioService.obterEnvioDetalhePorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.envioId());
        assertEquals("João Silva", result.nomeRemetente());
        assertEquals("Rua Teste, 123", result.endereco());
        assertNotNull(result.frete());
        assertEquals("25.50", result.frete().valorPAC());
        assertEquals("Cálculo realizado com sucesso", result.mensagemGeral());

        verify(envioRepository, times(1)).findById(1L);
        verify(freteRepository, times(1)).findByEnvioEnvioId(1L);
    }

    @Test
    @DisplayName("Deve obter detalhes do envio sem frete")
    void deveObterEnvioDetalheSemFrete() {

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(freteRepository.findByEnvioEnvioId(1L)).thenReturn(Optional.empty());

        var result = envioService.obterEnvioDetalhePorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.envioId());
        assertNull(result.frete());
        assertNull(result.mensagemGeral());

        verify(envioRepository, times(1)).findById(1L);
        verify(freteRepository, times(1)).findByEnvioEnvioId(1L);
    }

    @Test
    @DisplayName("Deve atualizar envio completo")
    void deveAtualizarEnvioCompleto() {

        var atualizarDTO = new AtualizarEnvioDTO(
                "Maria Santos",
                "Av. Paulista, 1000",
                "11111111",
                "22222222",
                15,
                25,
                35,
                7.5f
        );

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        doNothing().when(cepValidator).validar(anyString());
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        when(freteRepository.findByEnvioEnvioId(1L)).thenReturn(Optional.of(frete));
        when(freteCalculator.calcularFrete(anyString(), anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(freteInternoDTO);
        when(freteRepository.save(any(Frete.class))).thenReturn(frete);

        assertDoesNotThrow(() -> {
            envioService.atualizarEnvio(1L, atualizarDTO);
        });

        verify(envioRepository, times(1)).findById(1L);
        verify(cepValidator, times(2)).validar(anyString());
        verify(envioRepository, times(1)).save(any(Envio.class));
        verify(freteCalculator, times(1)).calcularFrete(anyString(), anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
        verify(freteRepository, times(1)).findByEnvioEnvioId(1L);
        verify(freteRepository, times(1)).save(any(Frete.class));
    }

    @Test
    @DisplayName("Deve validar CEPs ao atualizar")
    void deveValidarCepsAoAtualizar() {

        var atualizarDTO = new AtualizarEnvioDTO(
                "Maria Santos",
                "Av. Paulista, 1000",
                "11111111",
                "22222222",
                15,
                25,
                35,
                7.5f
        );

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        doThrow(new IllegalArgumentException("CEP inválido"))
                .when(cepValidator).validar("11111111");

        assertThrows(IllegalArgumentException.class, () -> {
            envioService.atualizarEnvio(1L, atualizarDTO);
        });

        verify(cepValidator, times(1)).validar("11111111");
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    @DisplayName("Deve atualizar envio parcialmente")
    void deveAtualizarEnvioParcialmente() {

        Map<String, Object> campos = new HashMap<>();
        campos.put("nomeRemetente", "Pedro Oliveira");
        campos.put("peso", 7.5);

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        when(freteRepository.findByEnvioEnvioId(1L)).thenReturn(Optional.of(frete));
        when(freteCalculator.calcularFrete(anyString(), anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(freteInternoDTO);
        when(freteRepository.save(any(Frete.class))).thenReturn(frete);

        assertDoesNotThrow(() -> {
            envioService.atualizarEnvioParcial(1L, campos);
        });

        verify(envioRepository, times(1)).findById(1L);
        verify(envioRepository, times(1)).save(any(Envio.class));
        verify(freteCalculator, times(1)).calcularFrete(anyString(), anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("Deve validar CEPs ao atualizar parcialmente")
    void deveValidarCepsAoAtualizarParcialmente() {

        Map<String, Object> campos = new HashMap<>();
        campos.put("cepOrigem", "11111111");
        campos.put("cepDestino", "22222222");

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        doNothing().when(cepValidator).validar(anyString());
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        when(freteRepository.findByEnvioEnvioId(1L)).thenReturn(Optional.of(frete));
        when(freteCalculator.calcularFrete(anyString(), anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(freteInternoDTO);
        when(freteRepository.save(any(Frete.class))).thenReturn(frete);

        assertDoesNotThrow(() -> {
            envioService.atualizarEnvioParcial(1L, campos);
        });

        verify(cepValidator, times(2)).validar(anyString());
        verify(cepValidator).validar("11111111");
        verify(cepValidator).validar("22222222");
    }

    @Test
    @DisplayName("Deve atualizar apenas CEP origem parcialmente")
    void deveAtualizarApenasCepOrigemParcialmente() {

        Map<String, Object> campos = new HashMap<>();
        campos.put("cepOrigem", "11111111");

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        doNothing().when(cepValidator).validar("11111111");
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        when(freteRepository.findByEnvioEnvioId(1L)).thenReturn(Optional.of(frete));
        when(freteCalculator.calcularFrete(anyString(), anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(freteInternoDTO);
        when(freteRepository.save(any(Frete.class))).thenReturn(frete);

        assertDoesNotThrow(() -> {
            envioService.atualizarEnvioParcial(1L, campos);
        });

        verify(cepValidator, times(1)).validar("11111111");
        verify(cepValidator, never()).validar("22222222");
    }

    @Test
    @DisplayName("Deve deletar envio com sucesso")
    void deveDeletarEnvioComSucesso() {

        when(envioRepository.existsById(1L)).thenReturn(true);
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(freteRepository.findByEnvioEnvioId(1L)).thenReturn(Optional.of(frete));
        doNothing().when(freteRepository).delete(any(Frete.class));
        doNothing().when(envioRepository).delete(any(Envio.class));

        assertDoesNotThrow(() -> {
            envioService.deleteEnvio(1L);
        });

        verify(envioRepository, times(1)).existsById(1L);
        verify(freteRepository, times(1)).findByEnvioEnvioId(1L);
        verify(freteRepository, times(1)).delete(frete);
        verify(envioRepository, times(1)).delete(envio);
    }

    @Test
    @DisplayName("Deve deletar envio sem frete associado")
    void deveDeletarEnvioSemFreteAssociado() {

        when(envioRepository.existsById(1L)).thenReturn(true);
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(freteRepository.findByEnvioEnvioId(1L)).thenReturn(Optional.empty());
        doNothing().when(envioRepository).delete(any(Envio.class));

        assertDoesNotThrow(() -> {
            envioService.deleteEnvio(1L);
        });

        verify(envioRepository, times(1)).existsById(1L);
        verify(freteRepository, times(1)).findByEnvioEnvioId(1L);
        verify(freteRepository, never()).delete(any(Frete.class));
        verify(envioRepository, times(1)).delete(envio);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar envio inexistente")
    void deveLancarExcecaoAoDeletarEnvioInexistente() {

        when(envioRepository.existsById(999L)).thenReturn(false);

        var exception = assertThrows(ResourceNotFoundException.class, () -> {
            envioService.deleteEnvio(999L);
        });

        assertEquals("Envio não encontrado com ID: 999", exception.getMessage());
        verify(envioRepository, times(1)).existsById(999L);
        verify(envioRepository, never()).delete(any(Envio.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao recalcular frete sem frete existente")
    void deveLancarExcecaoAoRecalcularFreteSemFreteExistente() {

        var atualizarDTO = new AtualizarEnvioDTO(
                "Maria Santos",
                "Av. Paulista, 1000",
                "11111111",
                "22222222",
                15,
                25,
                35,
                7.5f
        );

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        doNothing().when(cepValidator).validar(anyString());
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        when(freteRepository.findByEnvioEnvioId(1L)).thenReturn(Optional.empty());
        when(freteCalculator.calcularFrete(anyString(), anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(freteInternoDTO);

        var exception = assertThrows(ResourceNotFoundException.class, () -> {
            envioService.atualizarEnvio(1L, atualizarDTO);
        });

        assertTrue(exception.getMessage().contains("Frete não encontrado"));
        verify(freteRepository, times(1)).findByEnvioEnvioId(1L);
        verify(freteRepository, never()).save(any(Frete.class));
    }
}