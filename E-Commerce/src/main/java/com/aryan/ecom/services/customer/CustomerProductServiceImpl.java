package com.aryan.ecom.services.customer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aryan.ecom.dto.ProductDetailDto;
import com.aryan.ecom.dto.ProductDto;
import com.aryan.ecom.model.FAQ;
import com.aryan.ecom.model.Product;
import com.aryan.ecom.model.Review;
import com.aryan.ecom.repository.FAQRepository;
import com.aryan.ecom.repository.ProductRepository;
import com.aryan.ecom.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerProductServiceImpl implements CustomerProductService {

	private final ProductRepository productRepository;

	private final FAQRepository faqRepository;

	private final ReviewRepository reviewRepository;

	public List<ProductDto> getAllProducts() {
		List<Product> products = productRepository.findAll();
		return products.stream().map(Product::getDto).collect(Collectors.toList());
	}

	public List<ProductDto> getAllProductsByName(String name) {
		List<Product> products = productRepository.findAllByNameContaining(name);
		return products.stream().map(Product::getDto).collect(Collectors.toList());
	}

	public ProductDetailDto getProductDetailById(Long productId) {
		Optional<Product> optionalProduct = productRepository.findById(productId);
		if (optionalProduct.isPresent()) {
			List<FAQ> faqs = faqRepository.findAllByProductId(productId);
			List<Review> reviews = reviewRepository.findAllByProductId(productId);

			ProductDetailDto productDetailDto = new ProductDetailDto();
			productDetailDto.setProductDto(optionalProduct.get().getDto());
			productDetailDto.setFaqDtoList(faqs.stream().map(FAQ::getFAQDto).collect(Collectors.toList()));
			productDetailDto.setReviewDtoList(reviews.stream().map(Review::getDto).collect(Collectors.toList()));

			return productDetailDto;

		}
		return null;
	}
}
