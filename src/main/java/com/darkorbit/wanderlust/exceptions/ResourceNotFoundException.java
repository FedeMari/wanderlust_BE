package com.darkorbit.wanderlust.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/* Questa classe è stata definita solo per mappare il NOT FOUND direttamente 
 * su un qualsiasi errore di tipo FileNotFound (che dovrà essere chiamato
 * laddove previsto)
 * 
 * Per ogni eccezione dobbiamo fare una classe 
 */
@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message) {
		super(message);
	}
}
