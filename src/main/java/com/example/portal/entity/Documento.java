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
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "documento")
@Getter
@Setter
@NoArgsConstructor
public class Documento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 150)
	private String nome;

	@Column(length = 255)
	private String descricao;

	@Column(name = "nome_arquivo", nullable = false, length = 255)
	private String nomeArquivo;

	@Column(name = "tipo_arquivo", length = 50)
	private String tipoArquivo;

	private Long tamanho;

	@Column(name = "caminho_arquivo", columnDefinition = "TEXT")
	private String caminhoArquivo;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pessoa_id", nullable = false)
	private Pessoa pessoa;

	@Column(name = "data_upload", nullable = false)
	private Instant dataUpload = Instant.now();
}
