package com.example.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaUpdateRequest(
		@NotBlank(message = "Nome é obrigatório") @Size(max = 100) String nome,
		@NotBlank(message = "Tipo é obrigatório") @Size(max = 20) String tipo,
		boolean ativo
) {}
