package com.aryan.ecom.controller.customer;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aryan.ecom.dto.WishlistDto;
 import com.aryan.ecom.services.customer.wishlist.WishlistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class WishlistController {
	private final WishlistService wishlistService;
	
	@PostMapping("/wishlist")
	public ResponseEntity<?> addProductToWishlist(@RequestBody WishlistDto wishlistDto){
		WishlistDto posteWishlistDto = wishlistService.addProductToWishlist(wishlistDto);
		if(posteWishlistDto==null) return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("something went wrong");
		return ResponseEntity.status(HttpStatus.CREATED).body(posteWishlistDto);
	}
	
	@GetMapping("/wishlist/{userId}")
	public ResponseEntity<List<WishlistDto>> getWishlistByUserId(@PathVariable Long userId){
		return ResponseEntity.ok(wishlistService.getWishlistByUserId(userId));
	}
}
