package com.example.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ListaCompraUpdateRequest(
		@NotBlank @Size(max = 150) String nome
) {}
