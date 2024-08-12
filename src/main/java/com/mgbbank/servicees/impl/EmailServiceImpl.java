package com.mgbbank.servicees.impl;

import java.io.File;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.mgbbank.dto.EmailDetails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService{

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	private String sendrEmail;

	@Override
	public void sendEmailAlerts(EmailDetails emailDetails) {
		
		try {
			
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setFrom(sendrEmail);
			mailMessage.setTo(emailDetails.getRecipient());
			mailMessage.setText(emailDetails.getMessageBody());
			mailMessage.setSubject(emailDetails.getSubjects());
			
			javaMailSender.send(mailMessage);
			System.out.println("Mail Sent Successfuly");
			
		}catch (MailException e) {
			throw new RuntimeException(e);
		}
		
}

	@Override
	public void sendEmailWithAttachments(EmailDetails emailDetails) {
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimemessageHelper;
		
		try {
			mimemessageHelper = new MimeMessageHelper(mimeMessage,true);
			mimemessageHelper.setFrom(sendrEmail);
			mimemessageHelper.setTo(emailDetails.getRecipient());
			mimemessageHelper.setText(emailDetails.getMessageBody());
			mimemessageHelper.setSubject(emailDetails.getSubjects());
			
			FileSystemResource file = new FileSystemResource(new File(emailDetails.getAttachments()));
			 if (file.exists()) {
		            mimemessageHelper.addAttachment(file.getFilename(), file);
		        }
			javaMailSender.send(mimeMessage);
			
			log.info(file.getFilename() + "Has been sent to user with email " + emailDetails.getRecipient());
		}catch(MessagingException e){
			
			throw new RuntimeException(e);
		}
		
	}
}
