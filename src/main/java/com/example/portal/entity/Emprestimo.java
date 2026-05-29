package com.example.portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "emprestimo")
@Getter
@Setter
@NoArgsConstructor
public class Emprestimo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "id_login", nullable = false)
	private Long idLogin;

	@Column(nullable = false, length = 255)
	private String descricao;

	@Column(nullable = false, length = 40)
	private String banco;

	@Column(name = "valor_emprestimo", nullable = false, precision = 14, scale = 2)
	private BigDecimal valorEmprestimo;

	@Column(name = "quantidade_parcelas", nullable = false)
	private Integer quantidadeParcelas;

	@Column(name = "data_criacao", nullable = false, insertable = false, updatable = false)
	private Instant dataCriacao;
}
