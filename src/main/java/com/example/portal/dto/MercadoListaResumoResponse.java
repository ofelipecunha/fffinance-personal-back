package com.example.portal.dto;

import java.math.BigDecimal;

public record MercadoListaResumoResponse(
		int totalListas,
		BigDecimal valorTotalMes,
		int listasAbertas,
		int listasFinalizadas
) {}
