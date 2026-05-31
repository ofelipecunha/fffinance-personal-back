package com.example.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Atalho do dashboard: só descrição e quantidade. */
public record AnotacaoRapidoRequest(
		@NotBlank @Size(max = 150) String nome,
		@NotNull @Positive Integer quantidade
) {}
