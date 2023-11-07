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
import com.darkorbit.wanderlust.service.AuthService;
import com.fasterxml.jackson.annotation.*;

import jakarta.servlet.http.HttpServletRequest;


/* Nella struttura della mia WebApp ho scelto di gestire le operazioni
 * di BusinessLogic (quindi il package Service) direttamente in angular,
 * quindi nel FrontEnd. Potevamo scegliere anche di gestire il Service in
 * entrambi o nel BackEnd in quel caso avremmo dovuto aggiungere un package
 * e le operazioni qui effettuate dal controller trasportate nel Service e
 * nel controller avremmo solo richiamato i metodi del Service.
 * 
 */

// http://localhost:8080

@RestController
@RequestMapping("/api/v1")
public class UtenteController {
	@Autowired
	private UtenteDAO utenteDAO;
	
	@Autowired
	private AuthService authService;
	
	/* Decidiamo di usare un ResponseEntity anzichè un oggetto direttamente
	 * perchè in questo modo possiamo costruire direttamente la Response del
	 * Server che non solo includerà l'oggetto o la lista di oggetti nel 
	 * BODY, ma avrà anche uno STATUS CODE nella HTTP Response Line
	 */
	
	@GetMapping("/utenti")
	@JsonView(utenteView.class)
	public ResponseEntity<List<Utente>>  getAllUtenti() {
		var utenti = utenteDAO.findAll();
		return new ResponseEntity<>(utenti, HttpStatus.OK); 
	}
	
	/* Altra metodologia a sostituzione del @JsonViews. Le due tecniche
	 * differiscono per i motivi:
	   L'utilizzo dell'annotazione @JsonViews è una tecnica offerta dalla libreria Jackson per controllare la serializzazione/deserializzazione dei campi di un oggetto in base a una vista specifica. Puoi definire diverse viste, ognuna con un insieme diverso di campi da includere o escludere. Questo può essere utile quando hai la necessità di restituire solo alcuni campi dell'entità in determinate situazioni, ad esempio quando devi nascondere alcuni dati sensibili o ridurre la quantità di dati trasmessi tra il frontend e il backend.
	   D'altra parte, definire il codice interno ad ogni controller, magari utilizzando una mappa o una classe di trasformazione, ti offre un maggiore controllo sul processo di serializzazione/deserializzazione dei campi. Sicuramente verrà scritto più codice, ma puoi personalizzare completamente la logica di trasformazione dei dati, manipolare i campi in base alle tue esigenze specifiche e adottare approcci più flessibili.
	   @JsonViews un controllo granulare sui campi restituiti dall'entità in diverse situazioni,
	   Definire il codice interno per un maggiore controllo e flessibilità nella trasformazione dei dati.
	 * 
	 */
//	@GetMapping("/utenti")
//	public  ArrayList<HashMap<String, Object>> getAllUtenti() {
//	 	var utenti = new ArrayList<HashMap<String, Object>>();
//
//        utenteDAO.findAll().forEach(
//                (element) -> {
//                    var utenteMap = new HashMap<String, Object>();
//
//                    utenteMap.put("id_utente", element.getId_utente());
//                    utenteMap.put("nome", element.getNome());
//                    utenteMap.put("cognome", element.getCognome());
//                    utenteMap.put("email", element.getEmail());
//                    utenteMap.put("password", element.getPassword());
//
//                    utenti.add(utenteMap);
//                }
//        );
//        return utenti;
//	}
	
	/* Necessario un "mapping" diverso quindi se sopra è "utenti" qui metto
	 * "utente" che poi sarà seguito dalla parte variabile ID
	 */
	@GetMapping("/utente/{id}")
	@JsonView(utenteView.class)
	public ResponseEntity<Utente> getUtenteById(@PathVariable(value = "id") Long id) {
		Utente utente = utenteDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utente non trovato per questo ID : " + id));
		
		return new ResponseEntity<>(utente, HttpStatus.OK);
	}
	
	//LOGIN
	@PostMapping("/loginutente")
	public ResponseEntity<HashMap<String, Object>> findUtenteByLogin(@RequestBody Utente utente) {
		var loginUtente = new HashMap<String, Object>();
		
		String emailUtente = utente.getEmail();
		String passwordUtente = utente.getPassword();
		
		Optional<Utente> utenteAttuale = utenteDAO.findByEmail(emailUtente);
		
		if(utenteAttuale.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE); //406
		}
		if(utenteAttuale.get().getPassword().equals(passwordUtente)){
			loginUtente.put("id_utente", utenteAttuale.get().getId_utente());
			loginUtente.put("nome", utenteAttuale.get().getNome());
			loginUtente.put("cognome", utenteAttuale.get().getCognome());
			loginUtente.put("email", utenteAttuale.get().getEmail());
			loginUtente.put("password", utenteAttuale.get().getPassword());

			return new ResponseEntity<>(loginUtente, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
	}
	
	//REGISTRAZIONE
	@PostMapping("/add/u")
	public ResponseEntity<Utente> addUtente(HttpServletRequest request, @RequestBody Utente utente) {
		var listaUtenti = utenteDAO.findAll();
		
		for(Utente user: listaUtenti) {
			if(utente.getEmail().equals(user.getEmail())) {
				return new ResponseEntity<>(HttpStatus.CONFLICT); //409
			}
		}
		
		int k=0;	//indice controllo maiuscola
		int j=0;	//indice controllo numero
		int w=0;	//controllo spazio
		
		//password compresa tra i 5 e i 15 caratteri inclusi
		if(utente.getPassword().length()<5 || utente.getPassword().length()>15) {			
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);	//406		
		}
		else if(utente.getPassword().length()>4 && utente.getPassword().length()<16 ){
			for(int i=0; i<utente.getPassword().length();i++) {
				char x = utente.getPassword().charAt(i);
				if(x>=65 && x <=90) {
					k=1;
				}
				else if(x>=48 && x <=57) {
					j=1;					
				}
				else if(x==32) {
					w=1;					
				}
			}
		}
		
		if(w==1) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);	//401		
		}
		
		//lancia errore se almeno uno dei due è vero
		if(k==0 || j==0) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);	//406			
		}
		
		Utente newUtente = utenteDAO.save(utente);
		authService.sendRegistrationConfirm(newUtente);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		String location = request.getRequestURL().toString() + "/" + newUtente.getId_utente();
		responseHeaders.set("Location", location);
		
		MultiValueMap<String, String> map1 = new LinkedMultiValueMap<>();
		map1.set("Location", location);
		
		return new ResponseEntity<>(newUtente, map1 ,HttpStatus.CREATED);
		
		/* Se commentassimo le righe del <MultiValueMap> potremmo scrivere:
		  	return ResponseEntity
			.status(HttpStatus.CREATED)
			.headers(responseHeaders)
			.body(newUtente); 
		 */		
	}
	
	/* Necessario inserire tutti i campi per effettuare il PUT quindi
	 * anche l'ID altrimenti effettua un generico "add".
	 * Potremmo aggiungere un @RequestBody di tipo Post e fare le dovute 
	 * operazioni per aggiornare assieme ai dati dell'utente i campi come
	 * gli articoli scritti, ma a rigor di logica dovrebbe essere piu 
	 * corretto separare le due cose, quindi qui modifichiamo solo
	 * l'utente e nel controller del post facciamo la modifica dei Post.
	 */
	@PutMapping("/update/u")
	public ResponseEntity<Utente> updateUtente(@RequestBody Utente utente) {		
		if(utente.getPassword().length()<5) {			
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);	//406		
		}
		
		Utente updateUtente = utenteDAO.save(utente);				
		return new ResponseEntity<>(updateUtente, HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/u/{id}")
	public ResponseEntity<?> deleteUtenteById(@PathVariable(value = "id") Long id) {
		Utente utenteEliminato = utenteDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utente non trovato per questo ID : " + id));
		
		authService.sendDeleteAccountConfirm(utenteEliminato);
		
		utenteDAO.deleteById(id);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/articoliutente/{id}")
	@JsonView(utenteListaArticoli.class)
	public ResponseEntity<Utente> getArticoliUtenteById(@PathVariable(value = "id") Long id) {
		Utente utente = utenteDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utente non trovato per questo ID : " + id));
		
		return new ResponseEntity<>(utente, HttpStatus.OK);
	}	
	
//	@GetMapping("/articoliutente/{id}")
//	public ResponseEntity <HashMap<Object, List<Object>>> getArticoliUtenteById(@PathVariable(value = "id") Long id) {
//		Utente utente = utenteDAO.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException("Utente non trovato per questo ID : " + id));
//		var articoliUtente = new HashMap<Object, List<Object>>();
//		var articoliList = new ArrayList<Object>();
//		postDAO.findAll().forEach(
//				(elementPost) -> {
//					if(elementPost.getUtente_id().getId_utente()==id) {
//						articoliList.add(elementPost);
//					}								
//		});
//		articoliUtente.put(utente, articoliList);
//		return new ResponseEntity<>(articoliUtente, HttpStatus.OK);
//	}
	
	@GetMapping("/preferitiutente/{id}")
	@JsonView(utenteListaPreferiti.class)
	public ResponseEntity<Utente> getPreferitiUtenteById(@PathVariable(value = "id") Long id) {
		Utente utente = utenteDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utente non trovato per questo ID : " + id));
		
		return new ResponseEntity<>(utente, HttpStatus.OK);
	}
	
	//devi passare solo il body del post!!
	@PostMapping("/addpreferito/{id}")
	public ResponseEntity<Utente> addPreferitoUtenteById(@RequestBody Post post, @PathVariable(value = "id") Long id) {
		Utente utente = utenteDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utente non trovato per questo ID : " + id));
		
		List<Post> preferitiAttuali = new ArrayList<Post>();
		preferitiAttuali = utente.getPostPreferiti();
		
		List<Post> aggiungiPost = new ArrayList<Post>();
		aggiungiPost.addAll(preferitiAttuali);
		
		Iterator<Post> iterator = aggiungiPost.iterator();
		while (iterator.hasNext()) {
			Post postCorrente = iterator.next();
			if (post.getId_post() == postCorrente.getId_post()) {
				iterator.remove();
			}
		}
		
		aggiungiPost.add(post);
		
		utente.setPostPreferiti(aggiungiPost);		
		Utente utenteAggiornato = utenteDAO.save(utente);
		
		return new ResponseEntity<>(utenteAggiornato, HttpStatus.OK);
	}
	
	@DeleteMapping("/removepreferito/{idPost}/{id}")
	public ResponseEntity<?> removePreferitoUtenteById(@PathVariable(value = "idPost") Long idPost, @PathVariable(value = "id") Long id) {
		Utente utente = utenteDAO.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utente non trovato per questo ID : " + id));
		
		List<Post> preferitiAttuali = new ArrayList<Post>();
		preferitiAttuali = utente.getPostPreferiti();

		List<Post> aggiornamentoPost = new ArrayList<Post>();
		aggiornamentoPost = preferitiAttuali;
		
		/* PROBLEMA:
		 * <<ConcurrentModificationException è lanciata per interrompere 
		 * l'esecuzione quando qualcosa su cui stiamo iterando viene 
		 * modificato.>>
		 * 
		 * SOLUZIONE:
		 * Uso un iteratore per scorrere la lista dei preferiti 
		 * dell'utente. Quando troviamo il post da rimuovere, utilizziamo 
		 * il metodo iterator.remove() per eliminare l'elemento corrente 
		 * dalla lista in modo sicuro.
		 */
		
		Iterator<Post> iterator = aggiornamentoPost.iterator();
		while (iterator.hasNext()) {
			Post postCorrente = iterator.next();
			if (idPost == postCorrente.getId_post()) {
				iterator.remove();
			}
		}
		
		utente.setPostPreferiti(aggiornamentoPost);
		//Utente utenteAggiornato = 
		utenteDAO.save(utente);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
