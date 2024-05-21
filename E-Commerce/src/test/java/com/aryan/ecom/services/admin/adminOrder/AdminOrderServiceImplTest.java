package com.aryan.ecom.services.admin.adminOrder;

import com.aryan.ecom.dto.AnalyticsResponse;
import com.aryan.ecom.dto.OrderDto;
import com.aryan.ecom.enums.OrderStatus;
import com.aryan.ecom.enums.UserRole;
import com.aryan.ecom.model.*;
import com.aryan.ecom.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@Slf4j
class AdminOrderServiceImplTest {

    AutoCloseable autoCloseable;
    @Mock
    OrderRepository orderRepository;

    private AdminOrderService adminOrderService;

    private User user;
    private Category category;
    private Product product;
    private Coupon coupon;
    private Order order;
    private Order savedOrder;
    private OrderDto orderDto;
    private CartItems cartItem;
    private List<Order> orderList;
    private List<CartItems> cartItems;

    @BeforeEach
    void setUp() throws IOException {
        autoCloseable = MockitoAnnotations.openMocks(this);
        adminOrderService = new AdminOrderServiceImpl(orderRepository);

        user = User.builder()
                .email("demoEmail@mail.com")
                .name("demoName")
                .password(new BCryptPasswordEncoder().encode("demoPassword"))
                .role(UserRole.CUSTOMER)
                .build();


        category = Category.builder()
                .name("demoCategory")
                .description("demoDescription")
                .build();


        MultipartFile mockMultipartFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test image".getBytes());
        product = Product.builder()
                .name("demoName")
                .price(200L)
                .img(mockMultipartFile.getBytes())
                .category(category)
                .description("demoDescription")
                .build();

        cartItem = CartItems.builder()
                .product(product)
                .user(user)
                .quantity(2L)
                .build();
        cartItems = new ArrayList<>();
        cartItems.add(cartItem);

        coupon = Coupon.builder()
                .name("demoName")
                .code("FLAT50")
                .discount(50L)
                .expirationDate(new Date())
                .build();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);

        order = Order.builder()
                .orderDescription("demoDescription")
                .date(calendar.getTime())
                .amount(100L)
                .address("demoAddress")
                .payment("Done")
                .orderStatus(OrderStatus.Delivered)
                .totalAmount(500L)
                .discount(50L)
                .trackingId(UUID.randomUUID())
                .user(user)
                .coupon(coupon)
                .cartItems(cartItems)
                .build();

        orderDto = order.getOrderDto();

        savedOrder = Order.builder()
                .id(1L)
                .orderDescription("demoDescription")
                .date(new Date())
                .amount(100L)
                .address("demoAddress")
                .payment("Done")
                .orderStatus(OrderStatus.Shipped)
                .totalAmount(500L)
                .discount(50L)
                .trackingId(UUID.randomUUID())
                .user(user)
                .coupon(coupon)
                .cartItems(cartItems)
                .build();

        orderList = new ArrayList<>();
        orderList.add(savedOrder);

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getAllPlacedOrders() {
        when(orderRepository.findAllByOrderStatusIn(any())).thenReturn(orderList);
        assertEquals(orderList.size(), adminOrderService.getAllPlacedOrders().size());
        assertEquals(orderList.get(0).getOrderDto(), adminOrderService.getAllPlacedOrders().get(0));
    }

    @Test
    void changeOrderStatusToShipped() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        OrderDto result = adminOrderService.changeOrderStatus(1L, "Shipped");
        assertNotNull(result);
        assertEquals(OrderStatus.Shipped, result.getOrderStatus());
    }

    @Test
    void changeOrderStatusToDelivered() {
        savedOrder.setOrderStatus(OrderStatus.Delivered);
        orderDto.setOrderStatus(OrderStatus.Delivered);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderDto result = adminOrderService.changeOrderStatus(1L, "Delivered");

        assertNotNull(result);
        assertEquals(OrderStatus.Delivered, result.getOrderStatus());
    }

    @Test
    void changeOrderStatusOrderNotFound() {
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        OrderDto result = adminOrderService.changeOrderStatus(1L, "Shipped");

        assertNull(result);
    }

    @Test
    void calculateAnalytics() {
        orderList.get(0).setOrderStatus(OrderStatus.Delivered);
        when(orderRepository.findByDateBetweenAndOrderStatus(any(), any(), any(OrderStatus.class))).thenReturn(orderList);
        when(orderRepository.countByOrderStatus(OrderStatus.Placed)).thenReturn(0L);
        when(orderRepository.countByOrderStatus(OrderStatus.Delivered)).thenReturn(1L);
        when(orderRepository.countByOrderStatus(OrderStatus.Shipped)).thenReturn(1L);


        AnalyticsResponse response = adminOrderService.calculateAnalytics();
        assertEquals(1L, response.getDelivered());
        assertEquals(0L, response.getPlaced());
        assertEquals(1L, response.getCurrentMonthOrders());
        assertEquals(1L, response.getPreviousMonthOrders());
        assertEquals(100L, response.getCurrentMonthEarnings());
        assertEquals(100L, response.getPreviousMonthEarnings());
        assertEquals(orderList.get(0).getAmount(), response.getCurrentMonthEarnings());

    }

    // todo : FIX this test (1)
    @Test
    void testGetTotalOrdersForMonths() {
        int year = 2024;

        // Set start of March
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.MARCH); // March
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfMonth = calendar.getTime();

        // Set end of March
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endOfMonth = calendar.getTime();


        List<Order> orders = Arrays.asList(
                Order.builder()
                        .date(new Date(startOfMonth.getTime() + 24L*3600L*4L)) // Order in March
                        .orderStatus(OrderStatus.Delivered)
                        .amount(500L)
                        .build()
        );

        when(orderRepository.findByDateBetweenAndOrderStatus(startOfMonth, endOfMonth, OrderStatus.Delivered))
                .thenReturn(orders);

        long totalOrders = adminOrderService.getTotalOrdersForMonths(Calendar.MARCH+1, year);
        assertEquals(1L, totalOrders, "Should return 1 order for given month");
    }

    // todo : fix
    @Test
    void getTotalEarningsForMonth() {
        int year = 2024;

        // Set start of March
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.MARCH); // March
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfMonth = calendar.getTime();

        // Set end of March
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endOfMonth = calendar.getTime();


        List<Order> orders = new ArrayList<>();
        orders.add(Order.builder()
                        .date(new Date(startOfMonth.getTime() + 24L*3600L*4L)) // Order in March
                        .orderStatus(OrderStatus.Delivered)
                        .amount(500L)
                        .build()
        );

        when(orderRepository.findByDateBetweenAndOrderStatus(startOfMonth, endOfMonth, (OrderStatus.Delivered)))
                .thenReturn(orders);

        long totalAmount = adminOrderService.getTotalEarningsForMonth(Calendar.MARCH+1, year);
        assertEquals(500L, totalAmount, "Should return 500 for given month");
    }
}