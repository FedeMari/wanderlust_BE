package com.darkorbit.wanderlust.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;

import com.darkorbit.wanderlust.config.JsonViews.*;
import com.darkorbit.wanderlust.exceptions.ResourceNotFoundException;
import com.darkorbit.wanderlust.model.*;
import com.darkorbit.wanderlust.persistence.*;
import com.fasterxml.jackson.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v2")
public class PostController {
	@Autowired
	private PostDAO postDAO;
	@Autowired
	private UtenteDAO utenteDAO;

	@GetMapping("/posts")
	@JsonView(postView.class)
	public ResponseEntity<List<Post>>  getAllPosts() {
		var posts = postDAO.findAll();
		return new ResponseEntity<>(posts, HttpStatus.OK); 
	}

	@GetMapping("/post/{id}")
	@JsonView(postView.class)
	public ResponseEntity<Post> getPostById(@PathVariable(value = "id") Long id) {
		Post post = postDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post non trovato per questo ID : " + id));
		
		return new ResponseEntity<>(post, HttpStatus.OK);
	}
	
	@GetMapping("/autorePost/{id}")
	@JsonView(postAutore.class)
	public ResponseEntity<HashMap<String, String>> getAutorePostById(@PathVariable(value = "id") Long id) {
		Post post = postDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post non trovato per questo ID : " + id));
		
		var autore = "";
		var nome = post.getUtente_id().getNome();
		var cognome = post.getUtente_id().getCognome();
		
		autore = nome + " " + cognome;
		
		HashMap<String, String> response = new HashMap<>();
	    response.put("autore", autore);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// id post non viene passato perchè lo crea lui, passiamo id utente
	// per dare un riferimento a chi ha creato il post
	// infine passiamo i dati del post
	@PostMapping("/add/p/{id}")
	public ResponseEntity<Post> addPost(HttpServletRequest request, @RequestBody Post post, @PathVariable Long id) {
		Utente autore = utenteDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post non trovato per questo ID : " + id));
		post.setUtente_id(autore);
		
		Post newPost = postDAO.save(post);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		String location = request.getRequestURL().toString() + "/" + newPost.getId_post();
		responseHeaders.set("location", location);

		MultiValueMap<String, String> map1 = new LinkedMultiValueMap<>();
		map1.set("Location", location);
		
		return new ResponseEntity<>(newPost, map1 ,HttpStatus.CREATED);
	}
	
	
	@PutMapping("/update/p/{id}")
	public ResponseEntity<Post> updatePost(@RequestBody Post postAggiornato, @PathVariable Long id) {
		Utente autore = utenteDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utente non trovato per questo ID : " + id));
		postAggiornato.setUtente_id(autore);
		
		Post updatePost = postDAO.save(postAggiornato);				
		return new ResponseEntity<>(updatePost, HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/p/{id}")
	public ResponseEntity<?> deletePostById(@PathVariable(value = "id") Long id) {
		postDAO.deleteById(id);				
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//<List<String>> con return categorie e rimozione di categorieString e
	//HashMap per ottenere una lista di stringhe	
	@GetMapping("/categoriepost/{id}")
	@JsonView(postListaCategorie.class)
	public ResponseEntity <HashMap<String, String>> getCategoriePostById(@PathVariable(value = "id") Long id) {
		Post post = postDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post non trovato per questo ID : " + id));
		
		List<String> categorie = post.getTagPost() // il metodo ci consegna la lista di oggetti di tipo Categoria
				.stream() // Con la StreamAPI apriamo un flusso per poter lavorare con il risultato, quindi la lista
		        .map(Categoria::getNome_categoria) // Mappiamo ogni oggetto di tipo Categoria sul reference del nome della categoria che passiamo con il metodo 
		        .collect(Collectors.toList()); // Raccogliamo infine i nomi delle categorie ottenuti in una nuova lista

		// Uniamo i nomi delle categorie in una stringa separata da virgole
		
		String categorieString = String.join(", ", categorie);
		
		HashMap<String, String> response = new HashMap<>();
	    response.put("categorie", categorieString);
	    
	    /* Se volessi mantenere HashMap prova:
	     * for (int i = 0; i < categorie.size(); i++) {
		        String categoriaKey = "categoria" + (i + 1);
		        String categoriaValue = categorie.get(i);
		        response.put(categoriaKey, categoriaValue);
		    }
	     * 
	     */
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/immaginipost/{id}")
	@JsonView(postListaImmagini.class)
	public ResponseEntity<Post> getImmaginiPostById(@PathVariable(value = "id") Long id) {
		Post post = postDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post non trovato per questo ID : " + id));
		
		return new ResponseEntity<>(post, HttpStatus.OK);
	}
	
	//serve mettere l'id del post cui vogliamo aggiungere la categoria
	// e nel body i campi della categoriea incluso l'id_categoria !!!
	@PostMapping("/addcategoria/{id}")
	public ResponseEntity<Post> addCategoriePostById(@RequestBody Categoria categoria, @PathVariable(value = "id") Long id) {
		Post post = postDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post non trovato per questo ID : " + id));
		
		List<Categoria> categorieAttuali = new ArrayList<Categoria>();
		categorieAttuali = post.getTagPost();
		
		List<Categoria> aggiungiCategoria = new ArrayList<Categoria>();
		aggiungiCategoria.addAll(categorieAttuali);
		aggiungiCategoria.add(categoria);
		
		post.setTagPost(aggiungiCategoria);
		Post postAggiornato = postDAO.save(post);
		
		return new ResponseEntity<>(postAggiornato, HttpStatus.OK);
	}
	
}
