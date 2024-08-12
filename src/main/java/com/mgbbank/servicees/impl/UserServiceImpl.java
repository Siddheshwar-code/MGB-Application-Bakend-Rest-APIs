package com.mgbbank.servicees.impl;

import java.math.BigDecimal;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mgbbank.config.JwtTokenProvider;
import com.mgbbank.dto.AccountInfo;
import com.mgbbank.dto.BankResponse;
import com.mgbbank.dto.CreditDebitRequest;
import com.mgbbank.dto.EmailDetails;
import com.mgbbank.dto.EnquiryRequest;
import com.mgbbank.dto.LoginDto;
import com.mgbbank.dto.TransactionDto;
import com.mgbbank.dto.TranseferRequest;
import com.mgbbank.dto.UserRequest;
import com.mgbbank.entities.Role;
import com.mgbbank.entities.User;
import com.mgbbank.repositories.UserRepository;
import com.mgbbank.utils.AccountUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserServices{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Override
	public BankResponse createAccount(UserRequest userRequest) {
		
		if(userRepository.existsByEmail(userRequest.getEmail())) {
			
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
					.responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
					.accountInfo(null)
					.build();
		}
		
		User newUser = User.builder()
				
				.firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName())
				.otherName(userRequest.getOtherName())
				.gender(userRequest.getGender())
				.address(userRequest.getAddress())
				.stateOrigin(userRequest.getStateOrigin())
				.accountNumber(AccountUtils.generateAccountNumber())
				.accountBalance(BigDecimal.ZERO)
				.email(userRequest.getEmail())
				.password(passwordEncoder.encode(userRequest.getPassword()))
				.phoneNumber(userRequest.getPhoneNumber())
				.alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
				.status("ACTIVE")
				.role(Role.valueOf("ROLE_ADMIN"))
				.build(); 
		
		User savedUser =userRepository.save(newUser); 
		
		EmailDetails emailDetails = EmailDetails.builder()
				.recipient(savedUser.getEmail())
				.subjects("Account Creation")
				.messageBody("Congraculations! your account is successfully created. \nYour Account Details: \n"
						+ "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\nAccount Number: " +savedUser.getAccountNumber())
				.build();
		emailService.sendEmailAlerts(emailDetails);
		
		return BankResponse.builder()
				.responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
				.responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
				.accountInfo(AccountInfo.builder()
						.accountBalance(savedUser.getAccountBalance())
						.accountNumber(savedUser.getAccountNumber())
						.accountName(savedUser.getFirstName() + " " +savedUser.getLastName()+ " " +savedUser.getOtherName())
						.build())
				.build();
	}
	
	public BankResponse login(LoginDto loginDto) {
	    try {
	        // Attempt to authenticate the user
	        Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

	        // Create and send a login alert email
	        EmailDetails loginAlert = EmailDetails.builder()
	                .subjects("You're logged in")
	                .recipient(loginDto.getEmail())
	                .messageBody("You logged into your account. If you did not initiate this request, contact your bank.")
	                .build();
	        emailService.sendEmailAlerts(loginAlert);

	        // Return a response with a JWT token
	        return BankResponse.builder()
	                .responseCode("Login successful")
	                .responseMessage(jwtTokenProvider.generateToken(authentication))
	                .build();
	    } catch (AuthenticationException e) {
	        // Handle failed authentication
	        return BankResponse.builder()
	                .responseCode("Login failed")
	                .responseMessage("Invalid email or password")
	                .build();
	    }
	}

	
	@Override
	public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
		
		Boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
		if(!isAccountExists) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
					.responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
					.accountInfo(null)
					.build();
		}
		
		User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
		return BankResponse.builder()
				.responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
				.responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
				.accountInfo(AccountInfo.builder()
						.accountBalance(foundUser.getAccountBalance())
						.accountNumber(enquiryRequest.getAccountNumber())
						.accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
						.build())
				.build();
	}

	@Override
	public String nameEnquiry(EnquiryRequest enquiryRequest) {

		Boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
		if(!isAccountExists) {
			return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
	}
		User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
		
		return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();

}

	@Override
	public BankResponse creditAccount(CreditDebitRequest request) {
		
		Boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
		if(!isAccountExists) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
					.responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
					.accountInfo(null)
					.build(); 
		}
		
		User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
		userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmmount()));
		userRepository.save(userToCredit);
		
		TransactionDto transactionDto = TransactionDto.builder()
				.accountNumber(userToCredit.getAccountNumber())
				.transactionType("CREDIT")
				.ammount(request.getAmmount())
				.build();
		transactionService.saveTransaction(transactionDto);
		
		return BankResponse.builder()
				.responseCode(AccountUtils.ACCOUNT_CREDIT_SUCCESS)
				.responseMessage(AccountUtils.ACCOUNT_CREDIT_MESSAGE)
				.accountInfo(AccountInfo.builder()
						.accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
						.accountBalance(userToCredit.getAccountBalance())
						.accountNumber(userToCredit.getAccountNumber())
						.build())
				.build();
	}

	@Override
	public BankResponse debitAccount(CreditDebitRequest request) {
		
		Boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
		if(!isAccountExists) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
					.responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
					.accountInfo(null)
					.build(); 
		}
	User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
	
	BigInteger isAvailableBalance = userToDebit.getAccountBalance().toBigInteger();
	BigInteger debitAmmount = request.getAmmount().toBigInteger();
	
	if(isAvailableBalance.intValue() < debitAmmount.intValue()) {
		return BankResponse.builder()
				.responseCode(AccountUtils.INSUFICIENT_BALANCE_CODE)
				.responseMessage(AccountUtils.INSUFICIENT_BALANCE_MESSAGE)
				.accountInfo(null)
				.build();
		
		
		
	}else {
		userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmmount()));
		
		userRepository.save(userToDebit);
		
		TransactionDto transactionDto = TransactionDto.builder()
				.accountNumber(userToDebit.getAccountNumber())
				.transactionType("CREDIT")
				.ammount(request.getAmmount())
				.build();
		transactionService.saveTransaction(transactionDto);
		
		return BankResponse.builder()
				.responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
				.responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
				.accountInfo(AccountInfo.builder()
						.accountNumber(request.getAccountNumber())
						.accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
						.accountBalance(userToDebit.getAccountBalance())
						.build())
				.build();
		
  }
	
     }

	@Override
	public BankResponse transfer(TranseferRequest request) {
		
		
		boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
		
		if(!isDestinationAccountExist) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
					.responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
					.accountInfo(null)
					.build(); 
		}
		
		User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
		
		if(request.getAmmount().compareTo(sourceAccountUser.getAccountBalance())>0) {
			
			return BankResponse.builder()
					.responseCode(AccountUtils.INSUFICIENT_BALANCE_CODE)
					.responseMessage(AccountUtils.INSUFICIENT_BALANCE_MESSAGE)
					.accountInfo(null)
					.build();
		}
		
		sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmmount()));
		String sourceUserName = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() + " " + sourceAccountUser.getOtherName();
		
		userRepository.save(sourceAccountUser);
		EmailDetails debitAlert = EmailDetails.builder()
				.subjects("DEBIT ALERT")
				.recipient(sourceAccountUser.getEmail())
				.messageBody("The sum of " + request.getAmmount() + "Has Been deducted from your account! your current balance is " + sourceAccountUser.getAccountBalance())
				.build();
		
		emailService.sendEmailAlerts(debitAlert);
		
		User destinationAccounUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
		destinationAccounUser.setAccountBalance(destinationAccounUser.getAccountBalance().add(request.getAmmount()));
		
		//String recipientUsername = destinationAccounUser.getFirstName() + " " + destinationAccounUser.getLastName() + " " + destinationAccounUser.getOtherName();
		userRepository.save(destinationAccounUser);
		
		EmailDetails creditAlert = EmailDetails.builder()
				.subjects("CREDIT ALERT")
				.recipient(sourceAccountUser.getEmail())
				.messageBody("The ammount " + request.getAmmount() + "Has Been sent to your account from " + sourceUserName + " your current balance is " + sourceAccountUser.getAccountBalance())
				.build();
		
		emailService.sendEmailAlerts(creditAlert);
		
		TransactionDto transactionDto = TransactionDto.builder()
				.accountNumber(destinationAccounUser.getAccountNumber())
				.transactionType("CREDIT")
				.ammount(request.getAmmount())
				.build();
		transactionService.saveTransaction(transactionDto);
		
		return BankResponse.builder()
				.responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
				.responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
				.accountInfo(null)
				.build();
	}

	
	
}