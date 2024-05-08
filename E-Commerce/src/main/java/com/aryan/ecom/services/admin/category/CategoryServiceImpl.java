package com.aryan.ecom.services.admin.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aryan.ecom.dto.CategoryDto;
import com.aryan.ecom.model.Category;
import com.aryan.ecom.repository.CategoryRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	
	private final CategoryRepository categoryRepository;
	
	public Category createCategory(CategoryDto categoryDto) {

		return categoryRepository.save(
				Category.builder()
						.name(categoryDto.getName())
						.description(categoryDto.getDescription())
						.build()
		);
	}
	
	public List<Category> getAllCategory(){
		return categoryRepository.findAll();
	}
	
	
}
