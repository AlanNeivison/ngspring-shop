package com.aryan.ecom.controller.customer;

import java.util.List;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aryan.ecom.dto.AddProductInCartDto;
import com.aryan.ecom.dto.OrderDto;
import com.aryan.ecom.dto.PlaceOrderDto;
import com.aryan.ecom.exceptions.ValidationException;
import com.aryan.ecom.services.customer.cart.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CartController {
	private final CartService cartService;

	@PostMapping("/cart")
	public ResponseEntity<?> addProductToCart(@RequestBody AddProductInCartDto addProductInCartDto) {
		return cartService.addProductToCart(addProductInCartDto);
	}

	@GetMapping("/cart/{userId}")
	public ResponseEntity<?> getCartByUserId(@PathVariable Long userId) {
		OrderDto orderDto = cartService.getCartByUserId(userId);
		return ResponseEntity.status(HttpStatus.OK).body(orderDto);
	}

	@GetMapping("/coupon/{userId}/{code}")
	public ResponseEntity<?> applyCoupon(@PathVariable Long userId, @PathVariable String code) {
		try {
			OrderDto orderDto = cartService.applyCoupon(userId, code);
			return ResponseEntity.ok(orderDto);
		} catch (ValidationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/addition")
	public ResponseEntity<OrderDto> increaseProductQuantity(@RequestBody AddProductInCartDto addProductInCartDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(cartService.increaseProductQuantity(addProductInCartDto));
	}

	@PostMapping("/deduction")
	public ResponseEntity<OrderDto> decreaseProductQuantity(@RequestBody AddProductInCartDto addProductInCartDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(cartService.decreaseProductQuantity(addProductInCartDto));
	}

	@PostMapping("/placedOrder")
	public ResponseEntity<OrderDto> placeOrder(@RequestBody PlaceOrderDto placeOrderDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(cartService.placedOrder(placeOrderDto));
	}
	
	@GetMapping("/myOrders/{userId}")
	public ResponseEntity<List<OrderDto>> getMyPlacedOrders(@PathVariable Long userId){
		return ResponseEntity.ok(cartService.getMyPlacedOrders(userId));
	}

}
