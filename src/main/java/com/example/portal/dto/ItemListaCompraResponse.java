package com.example.portal.dto;

import java.math.BigDecimal;

public record ItemListaCompraResponse(
		Integer id,
		String nome,
		Integer quantidade,
		BigDecimal valorUnitario,
		BigDecimal total
) {}
