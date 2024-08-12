package com.mgbbank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mgbbank.entities.Transaction;

public interface TransactionRepository  extends JpaRepository<Transaction, String>{

}
