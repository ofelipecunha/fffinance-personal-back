package com.example.portal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContinhasCardResponse(
		Integer id,
		String descricao,
		BigDecimal valor,
		String tipo,
		Integer categoriaId,
		String categoria,
		Integer formaPagamentoId,
		String formaPagamento,
		String pago,
		LocalDate data,
		LocalDate dataPagamento) {}
