package com.campuseats.service;

import com.campuseats.domain.dto.AddItemRequest;
import com.campuseats.domain.dto.CartItemResponse;
import com.campuseats.domain.dto.CartResponse;
import com.campuseats.domain.entity.MenuItem;
import com.campuseats.domain.entity.Order;
import com.campuseats.domain.entity.OrderItem;
import com.campuseats.exception.NotFoundException;
import com.campuseats.exception.QuantityLimitException;
import com.campuseats.exception.SoldOutException;
import com.campuseats.mapper.CartMapper;
import com.campuseats.repository.MenuItemRepository;
import com.campuseats.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

// Rails equivalent: a service object whose methods run inside ActiveRecord::Base.transaction do ... end.
// If any exception is thrown, the whole transaction is rolled back.
@Service
@Transactional
@RequiredArgsConstructor
public class OrderItemService {

    private static final int MAX_QUANTITY = 10;

    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartMapper cartMapper;

    public CartResponse addItem(Order order, AddItemRequest req) {
        // Rails: MenuItem.find(params[:menu_item_id])
        MenuItem menuItem = menuItemRepository.findById(req.menuItemId())
                .orElseThrow(() -> new NotFoundException("Menu item " + req.menuItemId() + " not found"));

        if (menuItem.isSoldOut()) {
            throw new SoldOutException(menuItem.getName() + " is sold out");
        }

        OrderItem item = findItemForMenuItem(order, menuItem.getId());
        if (item != null) {
            int newQuantity = item.getQuantity() + req.quantity();
            if (newQuantity > MAX_QUANTITY) {
                throw new QuantityLimitException(
                        "You can order at most " + MAX_QUANTITY + " of " + menuItem.getName());
            }
            item.setQuantity(newQuantity);
        } else {
            // Rails: order.order_items.build(menu_item: menu_item, quantity: ...)
            item = new OrderItem();
            item.setOrder(order);
            item.setMenuItem(menuItem);
            item.setQuantity(req.quantity());
            order.getItems().add(item);
            orderItemRepository.save(item);
        }

        return buildCartResponse(order.getId(), cartMapper.toCartItemResponse(item));
    }

    public CartResponse removeItem(Order order, Long orderItemId) {
        OrderItem item = order.getItems().stream()
                .filter(i -> i.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Order item " + orderItemId + " not found in order " + order.getId()));

        // orphanRemoval = true deletes the row (Rails: has_many :order_items, dependent: :destroy)
        order.getItems().remove(item);

        return buildCartResponse(order.getId(), null);
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> listItems(Long orderId) {
        return orderItemRepository.findByOrderIdWithMenuItem(orderId).stream()
                .map(cartMapper::toCartItemResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal cartTotal(Long orderId) {
        return sumLineTotals(listItems(orderId));
    }

    private CartResponse buildCartResponse(Long orderId, CartItemResponse affectedItem) {
        List<CartItemResponse> items = listItems(orderId);
        int itemCount = items.stream().mapToInt(CartItemResponse::quantity).sum();
        return new CartResponse(affectedItem, sumLineTotals(items), itemCount);
    }

    private BigDecimal sumLineTotals(List<CartItemResponse> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemResponse item : items) {
            total = total.add(item.lineTotal());
        }
        return total;
    }

    private OrderItem findItemForMenuItem(Order order, Long menuItemId) {
        return order.getItems().stream()
                .filter(i -> i.getMenuItem().getId().equals(menuItemId))
                .findFirst()
                .orElse(null);
    }
}
