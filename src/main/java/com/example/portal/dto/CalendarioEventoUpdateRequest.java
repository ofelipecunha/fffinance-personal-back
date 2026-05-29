package com.example.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CalendarioEventoUpdateRequest(
		@NotBlank(message = "Descrição é obrigatória") @Size(max = 255) String descricao,
		@NotNull(message = "Data do evento é obrigatória") LocalDate dataEvento,
		@Size(max = 20) String tipo,
		@NotNull Boolean concluido
) {}
