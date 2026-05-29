package com.example.portal.dto;

import java.time.Instant;

/**
 * Payload de saída (lista e leituras) para forma de pagamento.
 */
public record FormaPagamentoResponse(Integer id, String nome, String tipo, Boolean ativo, Instant dataCriacao) {

}
