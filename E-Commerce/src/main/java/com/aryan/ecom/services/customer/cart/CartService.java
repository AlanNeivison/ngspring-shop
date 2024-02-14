package com.aryan.ecom.services.customer.cart;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.aryan.ecom.dto.AddProductInCartDto;
import com.aryan.ecom.dto.OrderDto;
import com.aryan.ecom.dto.PlaceOrderDto;

public interface CartService {
	ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto);

	OrderDto getCartByUserId(Long userId);
	
	OrderDto applyCoupon(Long userId,String code);
	
	OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto);
	
	OrderDto decreaseProductQuantity(AddProductInCartDto addProductInCartDto);
	
	OrderDto placeOrder(PlaceOrderDto placeOrderDto);
	
	List<OrderDto> getMyPlacedOrders(Long userId);
}
