package com.example.portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lancamento")
@Getter
@Setter
@NoArgsConstructor
public class Lancamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 255)
	private String descricao;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal valor;

	@Column(name = "data_lancamento", nullable = false)
	private LocalDate dataLancamento;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "categoria_id", nullable = false)
	private Categoria categoria;

	@Column(nullable = false, length = 20)
	private String tipo;

	@Column(name = "forma_pagamento", length = 50)
	private String formaPagamento;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "forma_pagamento_id")
	private FormaPagamento formaPagamentoRef;

	@Column(nullable = false)
	private Boolean pago = Boolean.FALSE;

	@Column(name = "data_criacao", nullable = false, insertable = false, updatable = false)
	private Instant dataCriacao;
}
