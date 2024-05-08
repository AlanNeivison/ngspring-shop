package com.aryan.ecom.services.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.aryan.ecom.dto.SignupRequest;
import com.aryan.ecom.dto.UserDto;
import com.aryan.ecom.enums.OrderStatus;
import com.aryan.ecom.enums.UserRole;
import com.aryan.ecom.model.Order;
import com.aryan.ecom.model.User;
import com.aryan.ecom.repository.OrderRepository;
import com.aryan.ecom.repository.UserRepository;

import jakarta.annotation.PostConstruct;

import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bEncoder;

    @Autowired
    private OrderRepository orderRepository;

    public UserDto createUser(SignupRequest signupRequest) {

        User createdUser = userRepository.save(User.builder()
                .email(signupRequest.getEmail())
                .name(signupRequest.getName())
                .password(bEncoder.encode(signupRequest.getPassword()))
                .role(UserRole.CUSTOMER)
                .build());

        Order order = new Order();
        order.setAmount(0L);
        order.setTotalAmount(0L);
        order.setDiscount(0L);
        order.setUser(createdUser);
        order.setOrderStatus(OrderStatus.Pending);
        orderRepository.save(order);

        UserDto userDto = new UserDto();
        userDto.setId(createdUser.getId());

        return userDto;

    }

    public Boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }

    @PostConstruct
    public void createAdminAccount() {
        log.info("Running application for first time creates an Admin account with default info");
        Optional<User> adminAccountUser = userRepository.findByRole(UserRole.ADMIN);
        if (adminAccountUser.isEmpty()) {
            User.builder()
                    .email("admin@gmail.com")
                    .name("admin")
                    .role(UserRole.ADMIN)
                    .password(new BCryptPasswordEncoder().encode("admin"))
                    .build();
        }
    }


}
