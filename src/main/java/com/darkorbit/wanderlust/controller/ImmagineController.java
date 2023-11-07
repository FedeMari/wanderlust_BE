package com.darkorbit.wanderlust.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;

import com.darkorbit.wanderlust.exceptions.ResourceNotFoundException;
import com.darkorbit.wanderlust.model.*;
import com.darkorbit.wanderlust.persistence.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v4")
public class ImmagineController {
	@Autowired
	private ImmagineDAO immagineDAO;
	@Autowired
	private PostDAO postDAO;

	@GetMapping("/immagini")
	public ResponseEntity<List<Immagine>>  getAllImmagini() {
		var immagini = immagineDAO.findAll();
		return new ResponseEntity<>(immagini, HttpStatus.OK); 
	}

	@GetMapping("/immagine/{id}")
	public ResponseEntity<Immagine> getImmagineById(@PathVariable(value = "id") Long id) {
		Immagine immagine = immagineDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Immagine non trovata per questo ID : " + id));
		
		return new ResponseEntity<>(immagine, HttpStatus.OK);
	}
	
	
	/* Per le immagini (diversamente dalle categorie che sono pre-esistenti)
	 * dovremo fare un discorso diverso. Qui l'utente non dovrà scegliere
	 * su una "base data di partenza", ma dovrà avere la facoltà di
	 * aggiungere le proprie immagini.
	 * 
	 * Saranno due i passaggi quindi, due chiamate:
	 * 
	 * 1) 	Aggiunta dell'immagine al DB senza collegamento al post
	 * 		[questo metodo]
	 * 
	 * 2) 	Successiva chiamata al metodo nel controller del Post per poter
	 * 		associare l'immagine aggiunta al post 
	 * 	
	 * ==> RISOLUZIONE:
	 * Per non fare due chiamate su ogni aggiunta gestiamo direttamente
	 * qui l'aggiunta dell'immagine, associandola ad un post tramite la sua
	 * chiave esterna. naturalmente andrà fatto per ogni immagine.
	 */
	
	@PostMapping("/add/i/{id}")
	public ResponseEntity<Immagine> addImmagine(HttpServletRequest request, @RequestBody Immagine immagine, @PathVariable Long id) {
		Post post = postDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post non trovato per questo ID : " + id));
		immagine.setPost_id(post);
		
		/* IMPORTANTE: in questo modo già possiamo aggiungere piu immagini
		 * se ne volessimo solo una dovremmo far si che tutte le altre immagini
		 * vengano cancellate
		 * 
		 */
		Immagine newImmagine = immagineDAO.save(immagine);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		String location = request.getRequestURL().toString() + "/" + newImmagine.getId_immagine();
		responseHeaders.set("location", location);

		MultiValueMap<String, String> map1 = new LinkedMultiValueMap<>();
		map1.set("Location", location);
		
		return new ResponseEntity<>(newImmagine, map1 ,HttpStatus.CREATED);
	}	
	
	
	/* Praticamente inutile in quanto lavoreremo sul front-end per liberare
	 * i campi se l'utente commette qualche errore o vuole cambiare
	 * 
	 * Alternativamente doppia chiamata:
	 * 1) Prima si cambia l'immagine (verrà persa l'associazione con il post)
	 * 2) Si richiama il metodo per riassociarla al post
	 */
	
	@PutMapping("/update/i/{id}")
	public ResponseEntity<Immagine> updateImmagine(@RequestBody Immagine immagineAggiornata, @PathVariable Long id) {
		Post post = postDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Post non trovato per questo ID : " + id));
		immagineAggiornata.setPost_id(post);
		
		Immagine updateImmagine = immagineDAO.save(immagineAggiornata);
		
		return new ResponseEntity<>(updateImmagine, HttpStatus.OK);
	}
	
	// Anche qui non possiamo cancellare un'immagine associata ad un post
	@DeleteMapping("/delete/i/{id}")
	public ResponseEntity<?> deleteImmagineById(@PathVariable(value = "id") Long id) {
		immagineDAO.deleteById(id);				
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
