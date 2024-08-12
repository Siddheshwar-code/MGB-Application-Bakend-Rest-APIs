package com.mgbbank.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionDto {

	private String transactionType;
	private BigDecimal ammount;
	private String accountNumber;
	private String status;
}
