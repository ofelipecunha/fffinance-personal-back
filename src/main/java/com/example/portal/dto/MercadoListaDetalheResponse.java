package com.example.portal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record MercadoListaDetalheResponse(
		Integer id,
		String nome,
		LocalDate dataCompra,
		Boolean finalizada,
		Long idLogin,
		String usuarioNome,
		BigDecimal valorTotal,
		List<ItemListaCompraResponse> itens
) {}
