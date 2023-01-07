package com.paymentchain.transactions.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private Double amount;
	private String channel;
	private String date;
	private String description;
	private String fee;
	private String ibanAccount;
	private String reference;
	private String status;
	
	

}
