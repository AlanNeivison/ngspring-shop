package com.aryan.ecom.services.customer.wishlist;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aryan.ecom.dto.WishlistDto;
import com.aryan.ecom.model.Product;
import com.aryan.ecom.model.User;
import com.aryan.ecom.model.Wishlist;
import com.aryan.ecom.repository.ProductRepository;
import com.aryan.ecom.repository.UserRepository;
import com.aryan.ecom.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService{
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final WishlistRepository wishlistRepository;
	
	public WishlistDto addProductToWishlist( WishlistDto wishlistDto) {
		Optional<Product> optionalProduct = productRepository.findById(wishlistDto.getProductId());
		Optional<User> optionalUser = userRepository.findById(wishlistDto.getUserId());
		
		if(optionalProduct.isPresent()&& optionalUser.isPresent()) {
			Wishlist wishlist = new Wishlist();
			wishlist.setProduct(optionalProduct.get());
			wishlist.setUser(optionalUser.get());
			
			return wishlistRepository.save(wishlist).getWishlistDto();
			
		}
		return null;
	}
	
}
