package com.campuseats.mapper;

import com.campuseats.domain.dto.CartItemResponse;
import com.campuseats.domain.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CartMapper {

    public CartItemResponse toCartItemResponse(OrderItem item) {
        BigDecimal price = item.getMenuItem().getPrice();
        return new CartItemResponse(
                item.getId(),
                item.getMenuItem().getName(),
                price,
                item.getQuantity(),
                price.multiply(BigDecimal.valueOf(item.getQuantity()))
        );
    }
}
