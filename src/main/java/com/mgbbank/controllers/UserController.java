package com.mgbbank.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mgbbank.dto.BankResponse;
import com.mgbbank.dto.CreditDebitRequest;
import com.mgbbank.dto.EnquiryRequest;
import com.mgbbank.dto.LoginDto;
import com.mgbbank.dto.TranseferRequest;
import com.mgbbank.dto.UserRequest;
import com.mgbbank.servicees.impl.UserServices;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("user")
@Tag(name = "User Account management APIs")
public class UserController {
	
	@Autowired
	UserServices userServices;
	
	@Operation(
			summary = "Create New User Account",
			description = "Creating a new User and assigning an account Id"
			)
	@ApiResponse(
			responseCode = "201",
			description = "Http status 201 CREATED"
			)
	
	@PostMapping
	public BankResponse createAccount(@RequestBody UserRequest userRequest) {
		
		return userServices.createAccount(userRequest);
	}
	
	@PostMapping("/login")
	public BankResponse login(@RequestBody LoginDto loginDto) {
		
		return userServices.login(loginDto);
	}
	
	
	@Operation(
			summary = "Balance Enquiry",
			description = "given Account number,check how much the user has"
			)
	@ApiResponse(
			responseCode = "200",
			description = "Http status 201 SUCCESS"
			)
	
	@GetMapping("/balanceEnquiry")
	public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
		
		return userServices.balanceEnquiry(enquiryRequest);
	}
	
	@GetMapping("/nameEnquiry")
	public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
		
		return userServices.nameEnquiry(enquiryRequest);
	}
	
	@PostMapping("/credit")
	public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
		
		return userServices.creditAccount(request);
	}
	
	@PostMapping("/debit")
	public BankResponse debitAccount(@RequestBody CreditDebitRequest request) {
		
		return userServices.debitAccount(request);
	}
	
	@PostMapping("/transfer")
	public BankResponse transfer(@RequestBody TranseferRequest request) {
		return userServices.transfer(request);
	}
}
