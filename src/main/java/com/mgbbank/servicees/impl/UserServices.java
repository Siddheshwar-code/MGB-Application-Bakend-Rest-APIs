package com.mgbbank.servicees.impl;

import com.mgbbank.dto.BankResponse;
import com.mgbbank.dto.CreditDebitRequest;
import com.mgbbank.dto.EnquiryRequest;
import com.mgbbank.dto.LoginDto;
import com.mgbbank.dto.TranseferRequest;
import com.mgbbank.dto.UserRequest;

public interface UserServices {

	BankResponse createAccount(UserRequest userRequest);
	
	BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
	
	String nameEnquiry(EnquiryRequest enquiryRequest);
	
	BankResponse creditAccount( CreditDebitRequest request);
	
	BankResponse debitAccount(CreditDebitRequest request);
	
	BankResponse transfer(TranseferRequest request);
	
	BankResponse login(LoginDto loginDto);
}
