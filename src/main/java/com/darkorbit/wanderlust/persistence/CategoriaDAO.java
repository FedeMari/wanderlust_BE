package com.darkorbit.wanderlust.persistence;

import com.darkorbit.wanderlust.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaDAO extends JpaRepository<Categoria, Long>{}
