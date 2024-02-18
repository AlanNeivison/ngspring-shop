package com.aryan.ecom.controller.admin;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import com.aryan.ecom.dto.FAQDto;
import com.aryan.ecom.dto.ProductDto;
import com.aryan.ecom.services.admin.adminproduct.AdminProductService;
import com.aryan.ecom.services.admin.faq.FAQService;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminProductController {

	private final AdminProductService adminProductService;

	private final FAQService faqService;

	@PostMapping("/product")
	public ResponseEntity<ProductDto> addProduct(@ModelAttribute ProductDto productDto) throws Exception {
		ProductDto productDto1 = adminProductService.addProduct(productDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(productDto1);
	}

	@GetMapping("/products")
	public ResponseEntity<List<ProductDto>> getAllProduct() {
		List<ProductDto> productDtos = adminProductService.getAllProducts();
		return ResponseEntity.ok(productDtos);
	}

	@GetMapping("/search/{name}")
	public ResponseEntity<List<ProductDto>> getAllProductByName(@PathVariable String name) {
		List<ProductDto> productDtos = adminProductService.getAllProductsByName(name);
		return ResponseEntity.ok(productDtos);
	}

	@DeleteMapping("/product/{productId}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
		boolean deleted = adminProductService.deleteProduct(productId);
		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	@PostMapping("/faq/{productId}")
	public ResponseEntity<FAQDto> postFAQ(@PathVariable Long productId, @RequestBody FAQDto faqDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(faqService.postFAQ(productId, faqDto));
	}
}
