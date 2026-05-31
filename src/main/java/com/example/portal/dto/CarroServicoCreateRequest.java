package com.example.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CarroServicoCreateRequest(
		@NotBlank(message = "Descrição é obrigatória") @Size(max = 255) String descricao,
		@NotNull(message = "Valor é obrigatório") @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero") BigDecimal valor,
		@NotNull(message = "Data é obrigatória") LocalDate dataLancamento
) {}
