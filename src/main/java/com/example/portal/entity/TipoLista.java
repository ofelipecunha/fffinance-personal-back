package com.example.portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_lista")
@Getter
@Setter
@NoArgsConstructor
public class TipoLista {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true, length = 32)
	private String codigo;

	@Column(nullable = false, length = 100)
	private String nome;

	@Column(length = 64)
	private String icone;

	@Column(length = 32)
	private String cor;

	@Column(nullable = false)
	private Integer ordem = 0;

	@Column(nullable = false)
	private Boolean ativo = Boolean.TRUE;
}
