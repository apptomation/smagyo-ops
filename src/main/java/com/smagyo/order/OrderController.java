package com.smagyo.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ── Public storefront ────────────────────────────────────────────────────

    /** POST /api/orders — place a new order from the checkout page */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(request);
    }

    // ── Admin (JWT required) ─────────────────────────────────────────────────

    /** GET /api/orders/admin?page=0&size=20 */
    @GetMapping("/admin")
    public Page<OrderDto> listForAdmin(Pageable pageable) {
        return orderService.listForAdmin(pageable);
    }

    /** PATCH /api/orders/admin/{id}/status?status=DELIVERED */
    @PatchMapping("/admin/{id}/status")
    public OrderDto updateStatus(@PathVariable String id, @RequestParam String status) {
        return orderService.updateStatus(id, status);
    }
}
