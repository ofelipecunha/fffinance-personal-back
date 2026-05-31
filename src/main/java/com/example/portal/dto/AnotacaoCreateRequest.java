package com.example.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnotacaoCreateRequest(@NotBlank @Size(max = 120) String titulo) {}
