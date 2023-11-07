package com.darkorbit.wanderlust.controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;

import com.darkorbit.wanderlust.config.JsonViews.*;
import com.darkorbit.wanderlust.exceptions.ResourceNotFoundException;
import com.darkorbit.wanderlust.model.*;
import com.darkorbit.wanderlust.persistence.*;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v3")
public class CategoriaController {
	@Autowired
	private CategoriaDAO categoriaDAO;

	@GetMapping("/categorie")
	@JsonView(categoriaView.class)
	public ResponseEntity<List<Categoria>>  getAllCategorie() {
		var categorie = categoriaDAO.findAll();
		return new ResponseEntity<>(categorie, HttpStatus.OK); 
	}

	@GetMapping("/categoria/{id}")
	@JsonView(categoriaView.class)
	public ResponseEntity<Categoria> getCategoriaById(@PathVariable(value = "id") Long id) {
		Categoria categoria = categoriaDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria non trovata per questo ID : " + id));
		
		return new ResponseEntity<>(categoria, HttpStatus.OK);
	}
	
	// quando facciamo le aggiunte dirette non serve l'id della categoria
	// deve essere generato automaticamente
	@PostMapping("/add/c")
	public ResponseEntity<Categoria> addCategoria(HttpServletRequest request, @RequestBody Categoria categoria) {
		Categoria newCategoria = categoriaDAO.save(categoria);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		String location = request.getRequestURL().toString() + "/" + newCategoria.getId_categoria();
		responseHeaders.set("location", location);

		MultiValueMap<String, String> map1 = new LinkedMultiValueMap<>();
		map1.set("Location", location);
		
		return new ResponseEntity<>(newCategoria, map1 ,HttpStatus.CREATED);
	}

	@PutMapping("/update/c")
	public ResponseEntity<Categoria> updateCategoria(@RequestBody Categoria categoria) {
		Categoria updateCategoria = categoriaDAO.save(categoria);				
		return new ResponseEntity<>(updateCategoria, HttpStatus.OK);
	}
	
	//se associata ad un post non è possibile eliminare una categoria
	@DeleteMapping("/delete/c/{id}")
	public ResponseEntity<?> deleteCategoriaById(@PathVariable(value = "id") Long id) {
		categoriaDAO.deleteById(id);				
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/articolicategoria/{id}")
	@JsonView(categoriaListaPost.class)
	public ResponseEntity<Categoria> getArticoliCategoriaById(@PathVariable(value = "id") Long id) {
		Categoria categoria = categoriaDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria non trovata per questo ID : " + id));
		
		return new ResponseEntity<>(categoria, HttpStatus.OK);
	}
}
