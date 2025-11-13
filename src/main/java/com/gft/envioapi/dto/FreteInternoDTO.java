package com.gft.envioapi.dto;

public record FreteInternoDTO(
        ServicoFreteDTO pac,
        ServicoFreteDTO sedex,
        String mensagem
) {
    public record ServicoFreteDTO(
            boolean disponivel,
            String valor,
            String prazo,
            String observacao
    ) {
        public static ServicoFreteDTO disponivel(String valor, String prazo) {
            return new ServicoFreteDTO(true, valor, prazo, null);
        }

        public static ServicoFreteDTO indisponivel(String observacao) {
            return new ServicoFreteDTO(false, null, null, observacao);
        }
    }
}