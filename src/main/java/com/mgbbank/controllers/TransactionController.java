package com.mgbbank.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.*;

import com.itextpdf.text.DocumentException;
import com.mgbbank.entities.Transaction;
import com.mgbbank.servicees.impl.BankStatement;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/bankstatement")

@AllArgsConstructor
public class TransactionController {

	@Autowired
	private BankStatement bankStatement;
	
	@GetMapping
	public List<Transaction>generateStatement(@RequestParam String accountNumber,
												@RequestParam String startDate,
												@RequestParam String endDate) throws FileNotFoundException, DocumentException {
		
		return bankStatement.generateStatement(accountNumber, startDate, endDate);
	}
}
