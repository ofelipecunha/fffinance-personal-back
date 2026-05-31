package com.example.portal.dto;

import java.time.Instant;

public record AnotacaoCardResponse(
		Integer id,
		String titulo,
		Instant dataCriacao,
		int qtdItens,
		Long idLogin,
		String usuarioNome
) {}
