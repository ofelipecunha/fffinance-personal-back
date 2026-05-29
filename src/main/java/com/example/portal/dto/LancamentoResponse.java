package com.example.portal.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record LancamentoResponse(
		Integer id,
		String descricao,
		BigDecimal valor,
		LocalDate dataLancamento,
		Integer categoriaId,
		String categoriaNome,
		String tipo,
		String formaPagamento,
		Integer formaPagamentoId,
		String formaPagamentoNome,
		Boolean pago,
		Instant dataCriacao
) {}
