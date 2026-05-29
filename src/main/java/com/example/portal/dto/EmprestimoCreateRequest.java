package com.example.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record EmprestimoCreateRequest(
		@NotBlank(message = "Descrição é obrigatória") @Size(max = 255) String descricao,
		@NotBlank(message = "Banco é obrigatório")
				@Pattern(
						regexp =
								"NUBANK|BANCO_DO_BRASIL|SANTANDER|ITAU|BRADESCO|BANCO_PAN|WILL|NEON",
						message = "Banco inválido")
				String banco,
		@NotNull(message = "Valor é obrigatório") @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
				BigDecimal valorEmprestimo,
		@NotNull(message = "Quantidade de parcelas é obrigatória")
				@Min(value = 1, message = "Informe ao menos 1 parcela")
				Integer quantidadeParcelas) {}
