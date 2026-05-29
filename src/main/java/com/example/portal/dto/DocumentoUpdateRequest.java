package com.example.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DocumentoUpdateRequest(
		@NotBlank @Size(max = 150) String nome,
		@Size(max = 255) String descricao,
		@NotNull Integer pessoaId) {}
