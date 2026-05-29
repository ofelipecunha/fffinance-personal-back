package com.example.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FormaPagamentoCreateRequest(
		@NotBlank(message = "Nome é obrigatório") @Size(max = 50) String nome,
		@Size(max = 30) String tipo,
		Boolean ativo
) {}
