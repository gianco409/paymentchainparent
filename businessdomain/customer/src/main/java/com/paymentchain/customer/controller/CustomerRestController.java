package com.paymentchain.customer.controller;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.repository.CustomerRepository;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@RestController
@RequestMapping("/customer")
public class CustomerRestController {

	@Autowired
	CustomerRepository customerRepository;

	private final WebClient.Builder webClientBuilder;

	public CustomerRestController(WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}

	// webClient requires HttpClient library to work propertly
	HttpClient client = HttpClient.create()
			// Connection Timeout: is a period within wich a connection between a client and
			// a server must be established
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500).option(ChannelOption.SO_KEEPALIVE, true)
			.option(EpollChannelOption.TCP_KEEPIDLE, 300).option(EpollChannelOption.TCP_KEEPINTVL, 60)
			// Response Timeout: The maximun time we wait to receive a response after
			// sending a request
			.responseTimeout(Duration.ofSeconds(1))
			/// Read and Write TImeout: A read timeout occurs when no data was read within a
			/// certain
			// period of time, while the write timeout when a write operation cannot finish
			/// at a specific time
			.doOnConnected(connection -> {
				connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
				connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
			});

	@GetMapping()
	public List<Customer> list() {
		return customerRepository.findAll();
	}

	@GetMapping("/{id}")
	public Customer get(@PathVariable long id) {
		return customerRepository.findById(id).get();
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> put(@PathVariable long id, @RequestBody Customer input) {
		Customer save = customerRepository.save(input);
		return ResponseEntity.ok(save);
	}

	@PostMapping
	public ResponseEntity<?> post(@RequestBody Customer input) {
		input.getProducts().forEach(x -> x.setCustomer(input));
		Customer save = customerRepository.save(input);
		return ResponseEntity.ok(save);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable long id) {
		Optional<Customer> findById = customerRepository.findById(id);

		if (findById.get() != null) {
			customerRepository.delete(findById.get());
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping("/full")
	public Customer getByCode(@RequestParam String code) {
		Customer customer = customerRepository.findByCode(code);

		List<CustomerProduct> products = customer.getProducts();

		products.forEach(x -> {
			String productName = getProductName(x.getId());
			x.setProductName(productName);
		});

		//find all transactions that belong this account number
		List<?> transactions = getTransactions(customer.getIban());
		customer.setTransactions(transactions);
		return customer;
	}

	private String getProductName(long id) {
		WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
				.baseUrl("http://localhost:8083/product")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", "http://localhost:8083/product")).build();

		JsonNode block = build.method(HttpMethod.GET).uri("/" + id).retrieve().bodyToMono(JsonNode.class).block();

		String name = block.get("name").asText();

		return name;
	}

	private List<?> getTransactions(String iban) {
		WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
				.baseUrl("http://localhost:8082/transaction")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

		List<?> transactions = build.method(HttpMethod.GET)
				.uri(uriBuilder -> uriBuilder.path("/customer/transactions").queryParam("ibanAccount", iban).build())
				.retrieve().bodyToFlux(Object.class).collectList().block();

		return transactions;
	}

}
