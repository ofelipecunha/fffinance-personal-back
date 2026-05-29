package com.example.portal.dto;

import java.math.BigDecimal;

public record ContinhasResumoResponse(
		BigDecimal totalReceitas,
		BigDecimal totalDespesas,
		BigDecimal despesasPagas,
		BigDecimal despesasPendentes,
		BigDecimal saldoPrevisto) {}
