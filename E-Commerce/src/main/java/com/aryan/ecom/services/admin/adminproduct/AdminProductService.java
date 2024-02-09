package com.aryan.ecom.services.admin.adminproduct;

import java.util.List;

import com.aryan.ecom.dto.ProductDto;

public interface AdminProductService {
	
	ProductDto addProduct(ProductDto productDto) throws Exception;
	
	List<ProductDto> getAllProducts();
}
