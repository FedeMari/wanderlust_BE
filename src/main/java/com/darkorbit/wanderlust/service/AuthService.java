package com.darkorbit.wanderlust.service;

import com.darkorbit.wanderlust.model.Utente;

public interface AuthService {
	
	void sendRegistrationConfirm(Utente utente);
	void sendDeleteAccountConfirm(Utente utente);

}
