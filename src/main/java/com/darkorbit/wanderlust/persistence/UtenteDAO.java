package com.darkorbit.wanderlust.persistence;

import com.darkorbit.wanderlust.model.Utente;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtenteDAO extends JpaRepository<Utente, Long> {
	public Optional<Utente> findByEmail(String email);
}
