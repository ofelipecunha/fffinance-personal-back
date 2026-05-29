package com.example.portal.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ListaCompraDetalheResponse(
		Integer id,
		Integer tipoId,
		String tipoCodigo,
		String tipoNome,
		String nome,
		Instant dataCriacao,
		BigDecimal valorTotal,
		List<ItemListaCompraResponse> itens
) {}
