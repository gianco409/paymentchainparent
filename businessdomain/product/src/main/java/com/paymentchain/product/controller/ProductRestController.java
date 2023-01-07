package com.paymentchain.product.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymentchain.product.entities.Product;
import com.paymentchain.product.repository.ProductRepository;

@RestController
@RequestMapping("/product")
public class ProductRestController {

	@Autowired
	ProductRepository ProductRepository;

	@GetMapping()
	public List<Product> list() {
		return ProductRepository.findAll();
	}

	@GetMapping("/{id}")
	public Product get(@PathVariable long id) {
		return ProductRepository.findById(id).get();
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> put(@PathVariable long id, @RequestBody Product input) {
		Product save = ProductRepository.save(input);
		return ResponseEntity.ok(save);
	}

	@PostMapping
	public ResponseEntity<?> post(@RequestBody Product input) {
		Product save = ProductRepository.save(input);
		return ResponseEntity.ok(save);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable long id) {
		Optional<Product> findById = ProductRepository.findById(id);

		if (findById.get() != null) {
			ProductRepository.delete(findById.get());
		}
		return ResponseEntity.ok().build();
	}

}
