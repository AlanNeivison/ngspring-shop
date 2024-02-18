package com.aryan.ecom.services.customer.review;

import java.io.IOException;

import com.aryan.ecom.dto.OrderedProductsResponseDto;
import com.aryan.ecom.dto.ReviewDto;

public interface ReviewService {
	
	ReviewDto giveReview(ReviewDto reviewDto) throws IOException ;
}
