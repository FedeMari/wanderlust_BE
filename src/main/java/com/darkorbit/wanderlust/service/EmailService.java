package com.darkorbit.wanderlust.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.darkorbit.wanderlust.model.Utente;

@Service
public class EmailService implements AuthService{
	
	@Autowired
	private JavaMailSender mailSender;
	
	private void sendEmail(String destinatario, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(destinatario);
		message.setSubject(subject);
		message.setText(body);
		
		mailSender.send(message);
	}
	
	
	@Override
	public void sendRegistrationConfirm(Utente utente) {
		String message = "Benvenuto in Wanderlust " + utente.getNome();
		sendEmail(utente.getEmail(), "Benvenuto in Wanderlust!", message);
	}


	@Override
	public void sendDeleteAccountConfirm(Utente utente) {
		String message = "Ci dispiace vederti andare via " + utente.getNome() + ", il tuo account è stato cancellato con successo.";
		sendEmail(utente.getEmail(), "A presto! Wanderlust!", message);		
	}
}
