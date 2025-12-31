package com.inventory.inventory.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateProductRequest(
    @NotBlank String name,
    @PositiveOrZero int quantity,
    @Positive double price,
    @NotNull Long supplierId
) {}