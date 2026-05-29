package com.example.portal.dto;

import java.time.Instant;
import java.time.LocalDate;

public record CalendarioEventoResponse(
		Integer id,
		String descricao,
		LocalDate dataEvento,
		String tipo,
		Boolean concluido,
		Instant dataCriacao
) {}
