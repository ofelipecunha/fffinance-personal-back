package com.example.portal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MercadoListaCardResponse(
		Integer id,
		String nome,
		LocalDate dataCompra,
		BigDecimal valorTotal,
		int qtdItens,
		Boolean finalizada,
		Long idLogin,
		String usuarioNome
) {}
