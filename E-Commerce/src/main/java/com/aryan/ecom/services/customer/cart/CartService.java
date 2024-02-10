package com.aryan.ecom.services.customer.cart;

import org.springframework.http.ResponseEntity;

import com.aryan.ecom.dto.AddProductInCartDto;

public interface CartService {
	ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto);
}
