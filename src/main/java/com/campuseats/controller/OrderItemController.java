package com.campuseats.controller;

import com.campuseats.domain.dto.AddItemRequest;
import com.campuseats.domain.dto.CartItemResponse;
import com.campuseats.domain.dto.CartResponse;
import com.campuseats.domain.entity.Order;
import com.campuseats.exception.NotFoundException;
import com.campuseats.repository.OrderRepository;
import com.campuseats.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Rails equivalent: OrderItemsController, nested route returning JSON
//   resources :orders, only: :show do
//     resources :items, controller: "order_items", only: [:index, :create, :destroy]
//   end
@RestController
@RequestMapping("/orders/{orderId}/items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    // Rails equivalent: before_action :set_order
    //   def set_order; @order = Order.find(params[:order_id]); end
    // Spring runs this before every handler below and puts the result in the model as "order".
    @ModelAttribute("order")
    public Order loadOrder(@PathVariable Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order " + orderId + " not found"));
    }

    // Rails: GET /orders/:order_id/items -> order_items#index (render json: ...)
    // binding = false: only reuse the loaded order, never copy request/path values onto it
    // (otherwise the {id} path variable on DELETE would overwrite order.id).
    @GetMapping
    public List<CartItemResponse> index(@ModelAttribute(name = "order", binding = false) Order order) {
        return orderItemService.listItems(order.getId());
    }

    // Rails: POST /orders/:order_id/items -> order_items#create
    // @Valid @RequestBody AddItemRequest = strong parameters:
    //   params.require(:order_item).permit(:menu_item_id, :quantity) + model validations
    // Rails: render json: ..., status: :created
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse create(@ModelAttribute(name = "order", binding = false) Order order,
                               @Valid @RequestBody AddItemRequest request) {
        return orderItemService.addItem(order, request);
    }

    // Rails: DELETE /orders/:order_id/items/:id -> order_items#destroy
    @DeleteMapping("/{id}")
    public CartResponse destroy(@ModelAttribute(name = "order", binding = false) Order order,
                                @PathVariable Long id) {
        return orderItemService.removeItem(order, id);
    }
}
