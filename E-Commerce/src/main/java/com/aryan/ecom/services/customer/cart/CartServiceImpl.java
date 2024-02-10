package com.aryan.ecom.services.customer.cart;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.aryan.ecom.dto.AddProductInCartDto;
import com.aryan.ecom.enums.OrderStatus;
import com.aryan.ecom.model.CartItems;
import com.aryan.ecom.model.Order;
import com.aryan.ecom.model.Product;
import com.aryan.ecom.model.User;
import com.aryan.ecom.repository.CartItemsRepository;
import com.aryan.ecom.repository.OrderRepository;
import com.aryan.ecom.repository.ProductRepository;
import com.aryan.ecom.repository.UserRepository;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartItemsRepository cartItemsRepository;

	@Autowired
	private ProductRepository productRepository;

	public ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto) {
		Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.Pending);
		Optional<CartItems> optionalCartItems = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
				addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId());

		if (optionalCartItems.isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		} else {
			Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
			Optional<User> optionalUser = userRepository.findById(addProductInCartDto.getUserId());
			if (optionalUser.isPresent() && optionalProduct.isPresent()) {
				CartItems cartItems = new CartItems();
				cartItems.setProduct(optionalProduct.get());
				cartItems.setPrice(optionalProduct.get().getPrice());
				cartItems.setQuantity(1L);
				cartItems.setUser(optionalUser.get());
				cartItems.setOrder(activeOrder);

				CartItems updatedCart = cartItemsRepository.save(cartItems);
				activeOrder.setAmount(activeOrder.getAmount() + cartItems.getPrice());
				activeOrder.getCartItems().add(cartItems);

				orderRepository.save(activeOrder);

				return ResponseEntity.status(HttpStatus.CREATED).body(cartItems);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or product not found");
			}

		}

	}

}
