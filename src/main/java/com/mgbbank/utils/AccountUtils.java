package com.mgbbank.utils;
import java.time.*;

public class AccountUtils {

	public static final String ACCOUNT_EXISTS_CODE = "001";
	public static final String ACCOUNT_EXISTS_MESSAGE = "This user allready has an Account";
	
	public static final String ACCOUNT_CREATION_SUCCESS = "002";
	public static final String ACCOUNT_CREATION_MESSAGE = "Account has been Successfuly created";
	
	public static final String ACCOUNT_NOT_EXISTS_CODE = "003";
	public static final String ACCOUNT_NOT_EXIST_MESSAGE ="User with provided account Number does not exists";
	
	public static final String ACCOUNT_FOUND_CODE = "004";
	public static final String ACCOUNT_FOUND_SUCCESS = "Account Found successfuly";
	
	public static final String ACCOUNT_CREDIT_SUCCESS = "005";
	public static final String ACCOUNT_CREDIT_MESSAGE = "Account Credited Successfully";
	
	public static final String INSUFICIENT_BALANCE_CODE = "006";
	public static final String INSUFICIENT_BALANCE_MESSAGE = "Insuficient Balance";
	
	public static final String ACCOUNT_DEBITED_SUCCESS = "007";
	public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account has been succesfully debited";
	
	public static final String TRANSFER_SUCCESS_CODE = "008";
	public static final String TRANSFER_SUCCESS_MESSAGE= "Transfer Successful";
	
	public static String generateAccountNumber() {
		
		Year currentYear = Year.now();
		
		int min=100000;
		int max=999999;
		
		int randNumber = (int) Math.floor(Math.random()*(max-min+1)+min);
		
		String year =String.valueOf(currentYear);
		
		String randomNumber=String.valueOf(randNumber);
		
		StringBuilder accountNumber = new StringBuilder();
		
		return accountNumber.append(year).append(randomNumber).toString();
	}
}

