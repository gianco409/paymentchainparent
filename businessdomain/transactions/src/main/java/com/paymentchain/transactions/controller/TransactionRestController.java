package com.paymentchain.transactions.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paymentchain.transactions.entities.Transaction;
import com.paymentchain.transactions.repository.TransactionRepository;

@RestController
@RequestMapping("/transaction")
public class TransactionRestController {


	@Autowired
	TransactionRepository transactionRepository;
	
//	private final WebClient.Builder webClientBuilder;
//
//	public CustomerRestController(WebClient.Builder webClientBuilder) {
//		this.webClientBuilder = webClientBuilder;
//	}
//	
//	//webClient requires HttpClient library to work propertly
//	HttpClient client = HttpClient.create()
//			//Connection Timeout: is a period within wich a connection between a client and a server must be established
//			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
//			.option(ChannelOption.SO_KEEPALIVE, true)
//			.option(EpollChannelOption.TCP_KEEPIDLE, 300)
//			.option(EpollChannelOption.TCP_KEEPINTVL, 60)
//			//Response Timeout: The maximun time we wait to receive a response after sending a request
//			.responseTimeout(Duration.ofSeconds(1))
//			/// Read and Write TImeout: A read timeout occurs when no data was read within a certain
//			//period of time, while the write timeout when a write operation cannot finish at a specific time
//			.doOnConnected(connection -> {
//				connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
//				connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
//			});
			
	@GetMapping()
	public List<Transaction> list() {
		return transactionRepository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Transaction> get(@PathVariable long id) {
		return transactionRepository.findById(id).map(x -> ResponseEntity.ok(x)).orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/customer/transactions")
	public List<Transaction> get(@RequestParam String ibanAccount) {
		return transactionRepository.findByIbanAccount(ibanAccount);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> put(@PathVariable long id, @RequestBody Transaction input) {
		Transaction find = transactionRepository.findById(id).get();
		
		if(find != null) {
			find.setAmount(input.getAmount());
			find.setChannel(input.getChannel());
			find.setDate(input.getDate());
			find.setDescription(input.getDescription());
			find.setFee(input.getFee());
			find.setIbanAccount(input.getIbanAccount());
			find.setReference(input.getReference());
			find.setStatus(input.getStatus());
		}
		
		Transaction save = transactionRepository.save(find);
		return ResponseEntity.ok(save);
	}

	@PostMapping
	public ResponseEntity<?> post(@RequestBody Transaction input) {
		Transaction save = transactionRepository.save(input);
		return ResponseEntity.ok(save);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable long id) {
		Optional<Transaction> findById = transactionRepository.findById(id);

		if (findById.get() != null) {
			transactionRepository.delete(findById.get());
		}
		return ResponseEntity.ok().build();
	}
	
//	@GetMapping("/full")
//	public Transaction getByCode(@RequestParam String code) {
//		Transaction customer = transactionRepository.findByCode(code);
//		
//		List<CustomerProduct> products = customer.getProducts();
//		
//		products.forEach(x ->{
//			String productName = getProductName(x.getId());
//			x.setProductName(productName);
//		});
//		
//		return customer;
//	}
	
//	private String getProductName(long id) {
//		WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
//				.baseUrl("http://localhost:8083/product")
//				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//				.defaultUriVariables(Collections.singletonMap("url", "http://localhost:8083/product"))
//				.build();
//		
//		JsonNode block = build.method(HttpMethod.GET).uri("/" + id)
//				.retrieve().bodyToMono(JsonNode.class).block();
//		
//		String name = block.get("name").asText();
//		
//		return name;
//	}



}
