package com.example.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoCreateRequest(
		@Size(max = 255) String descricao,
		@NotNull(message = "Valor é obrigatório") @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero") BigDecimal valor,
		@NotNull(message = "Data do lançamento é obrigatória") LocalDate dataLancamento,
		@NotNull(message = "Categoria é obrigatória") Integer categoriaId,
		@NotBlank(message = "Tipo é obrigatório") @Size(max = 20) String tipo,
		Integer formaPagamentoId,
		Boolean pago
) {}
