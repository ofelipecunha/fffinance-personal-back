package com.example.portal.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record ListaCompraCreateRequest(
		@NotNull Integer tipoId,
		@NotBlank @Size(max = 150) String nome,
		@Valid List<ItemListaLinhaRequest> itens
) {}
