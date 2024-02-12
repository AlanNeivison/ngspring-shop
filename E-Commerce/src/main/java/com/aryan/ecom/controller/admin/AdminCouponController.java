package com.aryan.ecom.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aryan.ecom.exceptions.ValidationException;
import com.aryan.ecom.model.Coupon;
import com.aryan.ecom.services.admin.coupon.AdminCouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {
	
	private final AdminCouponService adminCouponService;
	
	@PostMapping()
	public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon){
		if(coupon.getCode()==null) ResponseEntity.status(HttpStatus.CONFLICT).body("codeEmpty");
		try {
			System.out.println(coupon.toString());
			Coupon createdCoupon = adminCouponService.createCoupon(coupon);
			return ResponseEntity.ok(createdCoupon);
		}catch (ValidationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
