package com.aryan.ecom.services.admin.adminOrder;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aryan.ecom.dto.AnalyticsResponse;
import com.aryan.ecom.dto.OrderDto;
import com.aryan.ecom.enums.OrderStatus;
import com.aryan.ecom.model.Order;
import com.aryan.ecom.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService{
	private final OrderRepository orderRepository;
	
	public List<OrderDto> getAllPlacedOrders(){
		List<Order> orderList = orderRepository.findAllByOrderStatusIn(List.of(OrderStatus.Placed,OrderStatus.Shipped,OrderStatus.Delivered));
		return orderList.stream().map(Order::getOrderDto).collect(Collectors.toList());
	}
	
	public OrderDto changeOrderStatus(Long orderId,String status) {
		Optional<Order> optionalOrder = orderRepository.findById(orderId);
		if(optionalOrder.isPresent()) {
			Order order=optionalOrder.get();
			
			if(Objects.equals(status, "Shipped")) {
				order.setOrderStatus(OrderStatus.Shipped);
			}
			else if(Objects.equals(status, "Delivered")){
				order.setOrderStatus(OrderStatus.Delivered);
			}
			return orderRepository.save(order).getOrderDto();
		}
		return null;
	}
	
	public AnalyticsResponse calculateAnalytics() {
		LocalDate currentDate = LocalDate.now();
		LocalDate previousMonthDate = currentDate.minusMonths(1);
		
		Long currentMonthOrders = getTotalOrdersForMonths(currentDate.getMonthValue(),currentDate.getYear());
		Long previousMonthOrders = getTotalOrdersForMonths(previousMonthDate.getMonthValue(), previousMonthDate.getYear());
		
		Long currentMonthEarning = getTotalEarningsForMonth(currentDate.getMonthValue(),currentDate.getYear());
		Long previousMonthEarning = getTotalEarningsForMonth(previousMonthDate.getMonthValue(), previousMonthDate.getYear());
		
		Long placed = orderRepository.countByOrderStatus(OrderStatus.Placed);
		Long shipped = orderRepository.countByOrderStatus(OrderStatus.Shipped);
		Long delivered = orderRepository.countByOrderStatus(OrderStatus.Delivered);
		
		return new AnalyticsResponse(placed, shipped, delivered, currentMonthOrders, previousMonthOrders, currentMonthEarning, previousMonthEarning);
		
	}
	
	public Long getTotalOrdersForMonths(int month,int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.YEAR, year);
		calendar.set(calendar.MONTH, month-1);
		calendar.set(calendar.DAY_OF_MONTH, 1);
		calendar.set(calendar.HOUR_OF_DAY, 0);
		calendar.set(calendar.MINUTE, 0);
		calendar.set(calendar.SECOND, 0);
		
		 Date startOfMonth = calendar.getTime();
		 calendar.set(calendar.DAY_OF_MONTH, calendar.getActualMaximum(calendar.DAY_OF_MONTH));
		 calendar.set(calendar.HOUR_OF_DAY, 23);
		 calendar.set(calendar.MINUTE, 59);
		 calendar.set(calendar.SECOND, 59);
		 
		 Date endOfMonth = calendar.getTime();
		 List<Order> orders = orderRepository.findByDateBetweenAndOrderStatus(startOfMonth,endOfMonth,OrderStatus.Delivered);
		 
		 return (long)orders.size();
		 
	}
}
