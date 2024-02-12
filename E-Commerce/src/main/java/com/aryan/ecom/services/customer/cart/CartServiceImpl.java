package com.aryan.ecom.services.customer.cart;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.aryan.ecom.dto.AddProductInCartDto;
import com.aryan.ecom.dto.CartItemsDto;
import com.aryan.ecom.dto.OrderDto;
import com.aryan.ecom.enums.OrderStatus;
import com.aryan.ecom.exceptions.ValidationException;
import com.aryan.ecom.model.CartItems;
import com.aryan.ecom.model.Coupon;
import com.aryan.ecom.model.Order;
import com.aryan.ecom.model.Product;
import com.aryan.ecom.model.User;
import com.aryan.ecom.repository.CartItemsRepository;
import com.aryan.ecom.repository.CouponRepository;
import com.aryan.ecom.repository.OrderRepository;
import com.aryan.ecom.repository.ProductRepository;
import com.aryan.ecom.repository.UserRepository;

import jakarta.persistence.Id;

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
	
	@Autowired
	private CouponRepository couponRepository;

	public ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto) {

		System.out.println(addProductInCartDto.toString());

		Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(),
				OrderStatus.Pending);
		
		Optional<CartItems> optionalCartItems = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
				addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId());
		
		System.out.println(addProductInCartDto.getUserId() + " <=> " + OrderStatus.Pending);

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
				activeOrder.setTotalAmount(activeOrder.getTotalAmount() + cartItems.getPrice());
				activeOrder.setAmount(activeOrder.getAmount()+cartItems.getPrice());
				activeOrder.getCartItems().add(cartItems);

				orderRepository.save(activeOrder);
 
				return ResponseEntity.status(HttpStatus.CREATED).body(cartItems);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or product not found");
			}

		}

	}
	
	public OrderDto getCartByUserId(Long userId) {
		Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);
		List<CartItemsDto> cartItemsDtosList = activeOrder.getCartItems().stream().
												map(CartItems::getCartDto).collect(Collectors.toList());
		
		OrderDto orderDto = new OrderDto();
		orderDto.setId(activeOrder.getId());
		orderDto.setAmount(activeOrder.getAmount());
		orderDto.setOrderStatus(activeOrder.getOrderStatus());
		orderDto.setDiscount(activeOrder.getDiscount());
		orderDto.setTotalAmount(activeOrder.getTotalAmount());
		orderDto.setCartItems(cartItemsDtosList);
		
		if(activeOrder.getCoupon()!=null) {
			orderDto.setCouponName(activeOrder.getCoupon().getName());
		}
		
		return orderDto;
	}
	
	public OrderDto applyCoupon(Long userId,String code) {
		Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);
		Coupon coupon = couponRepository.findByCode(code).orElseThrow(()->new ValidationException("coupon not found"));
		if(couponIsExpired(coupon)) {
			throw new ValidationException("coupon is expired");
		}
		double discountAmount = ((coupon.getDiscount()/100.0)*activeOrder.getTotalAmount());
		double netAmount = activeOrder.getTotalAmount() - discountAmount;
		
		activeOrder.setAmount((long)netAmount);
		activeOrder.setDiscount((long)discountAmount);
		activeOrder.setCoupon(coupon);
		
		orderRepository.save(activeOrder);
		return activeOrder.getOrderDto();
		
	}
	
	public boolean couponIsExpired(Coupon coupon) {
		Date currentDate = new Date();
		Date expirationDate = coupon.getExpirationDate();
		return expirationDate !=null && currentDate.after(expirationDate);	
	}
	
	public OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto) {
		Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.Pending);
		Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
		Optional<CartItems> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(optionalProduct.get().getId(),activeOrder.getId(), addProductInCartDto.getUserId());
		
		if(optionalProduct.isPresent() && optionalCartItem.isPresent()) {
			CartItems cartItems = optionalCartItem.get();
			Product product = optionalProduct.get();
			activeOrder.setAmount(activeOrder.getAmount()+product.getPrice());
			activeOrder.setTotalAmount(activeOrder.getTotalAmount()+product.getPrice());
			cartItems.setQuantity(cartItems.getQuantity()+1);
			
			if(activeOrder.getCoupon() !=null) {
				double discountAmount = ((activeOrder.getCoupon().getDiscount()/100.0)*activeOrder.getTotalAmount());
				double netAmount = activeOrder.getTotalAmount() - discountAmount;
				
				activeOrder.setAmount((long)netAmount);
				activeOrder.setDiscount((long)discountAmount);
			}
			
			cartItemsRepository.save(cartItems);
			orderRepository.save(activeOrder);
			return activeOrder.getOrderDto();
		}
		return null;
		
	}

}
