package com.darkorbit.wanderlust.model;

import java.io.Serializable;
import java.util.*;

import com.darkorbit.wanderlust.config.JsonViews.postAutore;
import com.darkorbit.wanderlust.config.JsonViews.utenteListaArticoli;
import com.darkorbit.wanderlust.config.JsonViews.utenteListaPreferiti;
import com.darkorbit.wanderlust.config.JsonViews.utenteView;
import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;

@SuppressWarnings("serial")
@Entity(name="Utenti")
@Table(name = "Utenti")
public class Utente implements Serializable{
	long id_utente;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    
    /*Tutte le entità collegate a "molti" necessitano di una Collection
     * su cui lavorare
     **/
    private List<Post> articoli = new ArrayList<Post>();
    private List<Post> postPreferiti = new ArrayList<Post>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({utenteView.class, postAutore.class})
    @Column(nullable = false, updatable = false)
	public long getId_utente() {
		return id_utente;
	}
	public void setId_utente(long id_utente) {
		this.id_utente = id_utente;
	}
	
	@JsonView({utenteView.class, postAutore.class})
	@Column(name = "nome", nullable = false)
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	

	@JsonView({utenteView.class, postAutore.class})
	@Column(name = "cognome", nullable = false)
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	

	@JsonView(utenteView.class)
	@Column(name = "email", unique = true, nullable = false)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	

	@JsonView(utenteView.class)
	@Column(name = "password", nullable = false)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	/* Se volessimo aggiungere un CascadeType.ALL dobbiamo farlo sulla
	parte del "ONE", MAI SULLA PARTE DEL "MANY"*/
	/* La OneToMany viene mappata sul campo dell'entità corrispondente
	 * che richiama l'entità attuale
	 * Teoricamente qui quando viene serializzato un articolo 
	 * inserisce nuovamente il campo utente_id che genera il problema della
	 * ricorsione . Per evitarlo:
	 * - (verificato) @JsonIgnore sul campo nell'altra tabella
	 * - (teorico) usare qui @JsonIgnoreProperties("utente_id") come a dire
	 * << quando arrivi a serializzare quel campo ignoralo>>
     **/
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="utente_id")
	@JsonIgnoreProperties({"utente_id","utentiInteressati", "tagPost", "immaginiAssociate"})
	@JsonView(utenteListaArticoli.class)
	public List<Post> getArticoli() {
		return articoli;
	}
	public void setArticoli(List<Post> articoli) {
		this.articoli = articoli;
	}
	
	/* Di fatto qui indichiamo a Json di ignorare il campo "utentiInteressati"
	 * in quanto creerebbe una nuova ricorsione e lo stesso abbiamo fatto
	 * dall'altra parte.
	 * 
	 * Viene definito "owning side" quando definisce la tabella in genere
	 * nella prima entità
	 */
	@ManyToMany(fetch=FetchType.LAZY, targetEntity=Post.class)
	@JoinTable(name = "Favoriti", 
		joinColumns = @JoinColumn(name = "utente_id", referencedColumnName = "id_utente"), 
		inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id_post"))
	@JsonIgnoreProperties({"utente_id","utentiInteressati", "tagPost", "immaginiAssociate"})
	@JsonView(utenteListaPreferiti.class)
	public List<Post> getPostPreferiti() {
		return postPreferiti;
	}
	public void setPostPreferiti(List<Post> postPreferiti) {
		this.postPreferiti = postPreferiti;
	}
	
	
	public Utente() {}	
	
	public Utente(String nome, String cognome, String email, String password) {
		this.nome = nome;
		this.cognome = cognome;
		this.email = email;
		this.password = password;
	}
	
	
	@Override
	public String toString() {
		return "Utente [id_utente=" + id_utente + ", nome=" + nome + ", cognome=" + cognome + ", email=" + email
				+ ", password=" + password + "]";
	}
}


