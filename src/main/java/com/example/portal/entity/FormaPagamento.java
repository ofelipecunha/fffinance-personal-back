package com.example.portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "forma_pagamento")
@Getter
@Setter
@NoArgsConstructor
public class FormaPagamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 50)
	private String nome;

	@Column(length = 30)
	private String tipo;

	@Column(nullable = false)
	private Boolean ativo = Boolean.TRUE;

	@Column(name = "data_criacao", nullable = false, insertable = false, updatable = false)
	private Instant dataCriacao;
}
