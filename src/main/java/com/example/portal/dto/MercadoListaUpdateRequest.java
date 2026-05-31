package com.example.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record MercadoListaUpdateRequest(
		@NotBlank @Size(max = 100) String nome,
		LocalDate dataCompra,
		Boolean finalizada
) {}
