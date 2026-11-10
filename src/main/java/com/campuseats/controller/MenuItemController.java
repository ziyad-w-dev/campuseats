package com.campuseats.controller;

import com.campuseats.domain.dto.CartItemResponse;
import com.campuseats.domain.entity.Vendor;
import com.campuseats.exception.NotFoundException;
import com.campuseats.repository.MenuItemRepository;
import com.campuseats.repository.VendorRepository;
import com.campuseats.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

// Rails equivalent: MenuItemsController, nested route
//   resources :vendors do
//     resources :menu_items, only: :index
//   end
@Controller
@RequestMapping("/vendors/{vendorId}/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    // There is no login in this assignment, so order 1 is "the student's cart"
    // (Rails would use something like current_user.cart).
    private static final Long CART_ORDER_ID = 1L;

    private final VendorRepository vendorRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemService orderItemService;

    // Rails: GET /vendors/:vendor_id/menu_items -> menu_items#index
    @GetMapping
    public String index(@PathVariable Long vendorId, Model model) {
        // Rails: @vendor = Vendor.find(params[:vendor_id])
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new NotFoundException("Vendor " + vendorId + " not found"));

        List<CartItemResponse> cartItems = orderItemService.listItems(CART_ORDER_ID);

        model.addAttribute("vendor", vendor);
        model.addAttribute("menuItems", menuItemRepository.findByVendorId(vendorId));
        model.addAttribute("orderId", CART_ORDER_ID);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", orderItemService.cartTotal(CART_ORDER_ID));
        return "menu_items/index";
    }
}
