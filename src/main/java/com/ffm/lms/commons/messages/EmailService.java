package com.ffm.lms.commons.messages;

import java.io.File;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void send(String to, String subject, String text) throws Exception{
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("keeleantech@hotmail.com");
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}
	
	public void sendAttachment(String to, String subject, String text, String pathToAttachment) throws Exception{
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom("keeleantech@hotmail.com");
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(text);
		FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
		helper.addAttachment(pathToAttachment, file);
		mailSender.send(message);
	}
}
