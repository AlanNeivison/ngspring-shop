package com.aryan.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReviewDto {
	private Long id;

	private Long rating;

	private String description;

	private MultipartFile img;
	
	private byte[] returnedImg;

	private Long userId;

	private Long productId;
	
	private String username;
}
