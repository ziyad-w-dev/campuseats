package com.campuseats.repository;

import com.campuseats.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Rails equivalent: order.order_items.includes(:menu_item)
    // JOIN FETCH loads every item together with its menu item in ONE query,
    // instead of 1 query for the items + 1 query per item (the N+1 problem).
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.menuItem WHERE oi.order.id = :orderId ORDER BY oi.id")
    List<OrderItem> findByOrderIdWithMenuItem(@Param("orderId") Long orderId);
}
