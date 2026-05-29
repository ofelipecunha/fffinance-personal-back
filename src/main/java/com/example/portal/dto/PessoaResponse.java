package com.example.portal.dto;

import java.time.Instant;

public record PessoaResponse(Integer id, String nome, Boolean ativo, Instant dataCriacao) {}
