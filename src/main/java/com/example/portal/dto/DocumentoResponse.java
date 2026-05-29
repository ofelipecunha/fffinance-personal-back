package com.example.portal.dto;

import java.time.Instant;

public record DocumentoResponse(
		Integer id,
		String nome,
		String descricao,
		String nomeArquivo,
		String tipoArquivo,
		Long tamanho,
		String caminhoArquivo,
		Integer pessoaId,
		Instant dataUpload) {}
