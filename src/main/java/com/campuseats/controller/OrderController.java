package com.campuseats.controller;

import com.campuseats.domain.entity.Order;
import com.campuseats.exception.NotFoundException;
import com.campuseats.repository.OrderRepository;
import com.campuseats.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Rails equivalent: OrdersController
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    // Rails: GET /orders/:id -> orders#show, renders app/views/orders/show.html.erb
    @GetMapping("/orders/{orderId}")
    public String show(@PathVariable Long orderId, Model model) {
        // Rails: @order = Order.find(params[:id])
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order " + orderId + " not found"));

        model.addAttribute("order", order);
        model.addAttribute("orderId", order.getId());
        model.addAttribute("cartItems", orderItemService.listItems(orderId));
        model.addAttribute("cartTotal", orderItemService.cartTotal(orderId));
        return "orders/show";
    }
}
