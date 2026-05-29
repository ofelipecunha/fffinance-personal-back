package com.example.portal.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmprestimoResponse {
	Integer id;
	String descricao;
	String banco;
	BigDecimal valorEmprestimo;
	Integer quantidadeParcelas;
	Instant dataCriacao;
}
