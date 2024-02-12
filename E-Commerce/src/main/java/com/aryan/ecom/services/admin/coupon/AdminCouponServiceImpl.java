package com.aryan.ecom.services.admin.coupon;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aryan.ecom.exceptions.ValidationException;
import com.aryan.ecom.model.Coupon;
import com.aryan.ecom.repository.CouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminCouponServiceImpl implements AdminCouponService {
	private final CouponRepository couponRepository; 
	
	public Coupon createCoupon(Coupon coupon) {
		System.out.println(coupon.getCode());
		if(couponRepository.existsByCode(coupon.getCode())) {
			throw new ValidationException("Coupon code already exists");
		}else {
			return couponRepository.save(coupon);
		}
	}
	
	public List<Coupon> getAllCoupon(){
		return couponRepository.findAll();
	}
}
