package com.mgbbank.servicees.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mgbbank.dto.TransactionDto;
import com.mgbbank.entities.Transaction;
import com.mgbbank.repositories.TransactionRepository;

@Component
public class TransactionImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public void saveTransaction(TransactionDto transactionDto) {
		Transaction transaction = Transaction.builder()
				.transactionType(transactionDto.getTransactionType())
				.accountNumber(transactionDto.getAccountNumber())
				.ammount(transactionDto.getAmmount())
				.status("SUCCESS")
				.build();
		
		transactionRepository.save(transaction);
		System.out.println("Transaction Saved Successfully");
	}
	
	

}
