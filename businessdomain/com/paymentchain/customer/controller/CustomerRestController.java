package com.paymentchain.customer.controller;

import java.util.List;

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

import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.repository.CustomerRepository;

@RestController
@RequestMapping("/customer")
public class CustomerRestController {

	@Autowired
	CustomerRepository customerRepository;

	@GetMapping()
	public List<Customer> findAll() {
		return null;
	}

	@GetMapping("/{id}")
	public Customer get(@PathVariable long id) {
		return null;
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> put(@PathVariable long id, @RequestBody Customer input) {
		return null;
	}

	@PostMapping
	public ResponseEntity<?> post(@RequestBody Customer input) {
		Customer save = customerRepository.save(input);
		return ResponseEntity.ok(save);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable long id) {
		return null;
	}

}
