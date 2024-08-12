package com.mgbbank.servicees.impl;

import com.mgbbank.dto.EmailDetails;

public interface EmailService {

	void sendEmailAlerts(EmailDetails emailDetails);
	
	void sendEmailWithAttachments(EmailDetails emailDetails);
}
