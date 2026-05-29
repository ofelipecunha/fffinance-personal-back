package com.example.portal.dto;

import jakarta.validation.constraints.NotNull;

public record ContinhasPagoRequest(@NotNull Boolean pago) {}
