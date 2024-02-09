package com.aryan.ecom.services.admin.adminproduct;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aryan.ecom.dto.ProductDto;
import com.aryan.ecom.model.Category;
import com.aryan.ecom.model.Product;
import com.aryan.ecom.repository.CategoryRepository;
import com.aryan.ecom.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

	private final ProductRepository productRepository;

	private final CategoryRepository categoryRepository;

	public ProductDto addProduct(ProductDto productDto) throws Exception {
		Product product = new Product();
		product.setName(productDto.getName());
		product.setDescription(productDto.getDescription());
		product.setPrice(productDto.getPrice());
		product.setImg(productDto.getImg().getBytes());

		Category category = categoryRepository.findById(productDto.getCategoryId()).orElseThrow();
		product.setCategory(category);

		return productRepository.save(product).getDto();
	}

	public List<ProductDto> getAllProducts() {
		List<Product> products = productRepository.findAll();
		return products.stream().map(Product::getDto).collect(Collectors.toList());
	}

}
