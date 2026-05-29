package com.example.portal.dto;

import java.math.BigDecimal;

public record TipoListaCardResponse(
		Integer id,
		String codigo,
		String nome,
		String icone,
		String cor,
		Integer ordem,
		long qtdListas,
		BigDecimal valorTotal
) {}
