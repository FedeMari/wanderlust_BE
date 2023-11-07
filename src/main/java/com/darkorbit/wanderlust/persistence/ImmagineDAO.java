package com.darkorbit.wanderlust.persistence;

import com.darkorbit.wanderlust.model.Immagine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImmagineDAO extends JpaRepository<Immagine, Long>{}
