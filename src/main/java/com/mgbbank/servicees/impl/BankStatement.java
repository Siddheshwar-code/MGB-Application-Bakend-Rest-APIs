package com.mgbbank.servicees.impl;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mgbbank.dto.EmailDetails;
import com.mgbbank.entities.Transaction;
import com.mgbbank.entities.User;
import com.mgbbank.repositories.TransactionRepository;
import com.mgbbank.repositories.UserRepository;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;



@Component
@AllArgsConstructor
@Slf4j

public class BankStatement {
	
	    @Autowired
	    private final TransactionRepository transactionRepository;
	    private UserRepository userRepository;
	    private EmailService emailService;
	    
	    private static final String FILE = "C:\\Users\\aaa\\Documents\\MyStatement.pdf";

	    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
	      
	        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
	        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

	        
	        List<Transaction> transactionList = transactionRepository.findAll().stream()
	            .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
	            .filter(transaction -> {
	                LocalDate createdAtDate = transaction.getCreatedAt().toLocalDate();
	                return (!createdAtDate.isBefore(start) && !createdAtDate.isAfter(end));
	            })
	            .collect(Collectors.toList());
	        
	        User user = userRepository.findByAccountNumber(accountNumber);
	        String customerName = user.getFirstName() +" "+ user.getLastName() +" "+ user.getOtherName();
	        
	        Rectangle statementSize = new Rectangle(PageSize.A4);
	    	Document document = new Document( statementSize);
	    	log.info("Setting size of doccument");
	    	FileOutputStream outputStream = new FileOutputStream(FILE);
	    	PdfWriter.getInstance(document, outputStream);
	    	document.open();
	    	
	    	PdfPTable bankInfoTable = new PdfPTable(1);
	    	
	    	PdfPCell bankName = new PdfPCell(new Phrase("Maharashtra Gramin Bank"));
	    	bankName.setBorder(0);
	    	bankName.setBackgroundColor(BaseColor.BLUE);
	    	bankName.setPadding(20f);
	    	
	    	PdfPCell bankAddress =  new PdfPCell(new Phrase("Chatrapati Shivaji Maharaj Chauk, Shiradhon-413528"));
	    	bankAddress.setBorder(0);
	    	bankInfoTable.addCell(bankName);
	    	bankInfoTable.addCell(bankAddress);
	    	
	    	PdfPTable statementInfo = new PdfPTable(2);
	    	
	    	PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: "+startDate));
	    	customerInfo.setBorder(0);
	    	
	    	PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
	    	statement.setBorder(0);
	    	
	    	PdfPCell stopDate = new PdfPCell(new Phrase("End Date: "+endDate));
	    	stopDate.setBorder(0);
	    	
	    	PdfPCell name = new PdfPCell(new Phrase("Customer Name: "+customerName));
	    	name.setBorder(0);
	    	
	    	PdfPCell space = new PdfPCell();
	    	space.setBorder(0);
	    	
	    	PdfPCell address = new PdfPCell(new Phrase("Customer Address: "+user.getAddress()));
	    	address.setBorder(0);
	    	
	    	PdfPTable transactionTable = new PdfPTable(4);
	    	PdfPCell date = new PdfPCell(new Phrase("DATE"));
	    	date.setBackgroundColor(BaseColor.BLUE);
	    	date.setBorder(0);
	    	
	    	
	    	PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
	    	transactionType.setBackgroundColor(BaseColor.BLUE);
	    	transactionType.setBorder(0);
	    	
	    	PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
	    	transactionAmount.setBackgroundColor(BaseColor.BLUE);
	    	transactionAmount.setBorder(0);
	    	
	    	PdfPCell status = new PdfPCell(new Phrase("STATUS"));
	    	status.setBackgroundColor(BaseColor.BLUE);
	    	status.setBorder(0);
	    	
	    	transactionTable.addCell(date);
	    	transactionTable.addCell(transactionType);
	    	transactionTable.addCell(transactionAmount);
	    	transactionTable.addCell(status);
	    	
	    	transactionList.forEach(transaction -> {
	    		transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
	    		transactionTable.addCell(new Phrase(transaction.getTransactionType()));
	    		transactionTable.addCell(new Phrase(transaction.getAmmount().toString()));
	    		transactionTable.addCell(new Phrase(transaction.getStatus()));
	    	});
	    	
	    	statementInfo.addCell(customerInfo);
	    	statementInfo.addCell(statement);
	    	statementInfo.addCell(endDate);
	    	statementInfo.addCell(name);
	    	statementInfo.addCell(space);
	    	statementInfo.addCell(address);
	    	
	    	document.add(bankInfoTable);
	    	document.add(statementInfo);
	    	document.add(transactionTable);
	    	
	    	document.close();
	    	
	    	EmailDetails emailDetails = EmailDetails.builder()
	    			.recipient(user.getEmail())
	    			.subjects("STATEMENT OF ACCOUNT")
	    			.messageBody("Kindly find you requsted account statement attachment!")
	    			.attachments(FILE)
	    			.build();
	    	
	    	emailService.sendEmailWithAttachments(emailDetails);

	        return transactionList;
	    }

}

