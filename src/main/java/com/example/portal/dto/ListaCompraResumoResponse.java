package com.example.portal.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ListaCompraResumoResponse(
		Integer id,
		String nome,
		Instant dataCriacao,
		BigDecimal valorTotal
) {}
