package com.example.portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "calendario_evento")
@Getter
@Setter
@NoArgsConstructor
public class CalendarioEvento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 255)
	private String descricao;

	@Column(name = "data_evento", nullable = false)
	private LocalDate dataEvento;

	@Column(nullable = false, length = 20)
	private String tipo = "NOTACAO";

	@Column(nullable = false)
	private Boolean concluido = Boolean.FALSE;

	@Column(name = "data_criacao", nullable = false, insertable = false, updatable = false)
	private Instant dataCriacao;
}
