package com.campuseats.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddItemRequest(
        @NotNull Long menuItemId,
        @NotNull @Min(1) @Max(10) Integer quantity
) {}
