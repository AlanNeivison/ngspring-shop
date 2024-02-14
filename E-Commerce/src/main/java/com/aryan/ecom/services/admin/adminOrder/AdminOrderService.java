package com.aryan.ecom.services.admin.adminOrder;

import java.util.List;

import com.aryan.ecom.dto.OrderDto;

public interface AdminOrderService {
	List<OrderDto> getAllPlacedOrders();
}
