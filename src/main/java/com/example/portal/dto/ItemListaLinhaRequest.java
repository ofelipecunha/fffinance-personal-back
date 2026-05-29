package com.example.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ItemListaLinhaRequest(
		@NotBlank @Size(max = 150) String nome,
		@NotNull @Positive Integer quantidade,
		@NotNull BigDecimal valorUnitario
) {}
