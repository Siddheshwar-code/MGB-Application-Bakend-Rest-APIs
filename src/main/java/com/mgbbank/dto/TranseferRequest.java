package com.mgbbank.dto;

import java.math.BigDecimal;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TranseferRequest {

	private String sourceAccountNumber;
	
	private String destinationAccountNumber;
	
	private BigDecimal ammount;
}
