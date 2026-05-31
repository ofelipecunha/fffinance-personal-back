package com.example.portal.dto;

import java.time.Instant;
import java.util.List;

public record AnotacaoDetalheResponse(
		Integer id,
		String titulo,
		Instant dataCriacao,
		List<AnotacaoItemResponse> itens
) {}
