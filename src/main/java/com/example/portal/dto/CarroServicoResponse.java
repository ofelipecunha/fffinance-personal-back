package com.example.portal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CarroServicoResponse(
		Integer id,
		String descricao,
		BigDecimal valor,
		LocalDate dataLancamento
) {}
