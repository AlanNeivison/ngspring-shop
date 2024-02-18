package com.aryan.ecom.controller.customer;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aryan.ecom.dto.OrderedProductsResponseDto;
import com.aryan.ecom.dto.ReviewDto;
import com.aryan.ecom.services.customer.review.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class ReviewController {
	public final ReviewService reviewService;
	
	@GetMapping("/ordered-products/{orderId}")
	public ResponseEntity<OrderedProductsResponseDto> getOrderedProductDetailsByOrderId(@PathVariable Long orderId){
		return ResponseEntity.ok(reviewService.getOrderedProductsDetailsByOrderId(orderId));
	}
	

