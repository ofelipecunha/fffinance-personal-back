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
@Table(name = "anotacao_mercado")
@Getter
@Setter
@NoArgsConstructor
public class AnotacaoMercado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "id_login")
	private Long idLogin;

	@Column(nullable = false, length = 120)
	private String titulo;

	@Column(name = "data_criacao", nullable = false)
	private Instant dataCriacao = Instant.now();

	@Column(nullable = false)
	private Boolean convertida = Boolean.FALSE;

	@Column(name = "lista_id")
	private Integer listaId;
}
