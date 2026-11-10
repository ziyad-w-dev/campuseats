package com.campuseats.domain.dto;

import java.math.BigDecimal;

// One cart row as JSON. Rails equivalent: a jbuilder partial / as_json(only: ...)
public record CartItemResponse(
        Long id,
        String menuItemName,
        BigDecimal price,
        Integer quantity,
        BigDecimal lineTotal
) {
}
