package com.campuseats.domain.dto;

import java.math.BigDecimal;

public record CartResponse(
        CartItemResponse item,
        BigDecimal cartTotal,
        int itemCount
) {
}
