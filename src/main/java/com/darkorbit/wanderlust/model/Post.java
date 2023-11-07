package com.darkorbit.wanderlust.model;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.darkorbit.wanderlust.config.JsonViews.*;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

@SuppressWarnings("serial")
@Entity(name="Posts")
@Table(name = "Posts")
public class Post implements Serializable{
	private long id_post;
    private Date data;
    private String titolo;
    @Lob
    private String contenuto;
    private Utente utente_id;
    
    //private String autoreBackup;
    //private String immagineBackup;
    
    private List<Utente> utentiInteressati = new ArrayList<Utente>();    
    private List<Categoria> tagPost = new ArrayList<Categoria>();
    private List<Immagine> immaginiAssociate = new ArrayList<Immagine>();

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({utenteListaArticoli.class, utenteListaPreferiti.class, 
		postView.class, categoriaListaPost.class})
	public long getId_post() {
		return id_post;
	}
	public void setId_post(long id_post) {
		this.id_post = id_post;
	}
	
	@JsonView({utenteListaArticoli.class, utenteListaPreferiti.class, 
		postView.class})
	@Column(name = "data")
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	
	@JsonView({utenteListaArticoli.class, utenteListaPreferiti.class, 
		postView.class, categoriaListaPost.class})
	@Column(name = "titolo", unique = true, nullable = false)
	public String getTitolo() {
		return titolo;
	}
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	
	@JsonView({utenteListaArticoli.class, utenteListaPreferiti.class, 
		postView.class})
	@Column(name = "contenuto", nullable = false)
	public String getContenuto() {
		return contenuto;
	}
	public void setContenuto(String contenuto) {
		this.contenuto = contenuto;
	}
	
	/* Qui abbiamo dettato di ignorare direttamente la proprietà da Json
	 * con la annotazione JoinColumn invece indichiamo il nome della colonna
	 *  che referenzia la chiave primaria nel DB  
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "utente_id")
	@JsonView(postAutore.class)
	@JsonIgnoreProperties({"articoli", "postPreferiti", 
		"hibernateLazyInitializer", "handler"})
	public Utente getUtente_id() {
		return utente_id;
	}
	public void setUtente_id(Utente utente_id) {
		this.utente_id = utente_id;
	}
	
	/*In questo caso per evitare ricorsione abbiamo detto a Json di non 
	 * considerare le proprietà che genererebbero un richiamo 
	 * 
	 * Avendo aggiunto @JsonIgnore sulle proprietà direttamente nell'entità
	 * principale forse possiamo anche rimuovere da qui il JsonIgnoreProp.
	 */	
	@ManyToMany(mappedBy="postPreferiti", targetEntity=Utente.class)
	@JsonIgnoreProperties({"postPreferiti", "articoli"})
	@JsonIgnore
	public List<Utente> getUtentiInteressati() {
		return utentiInteressati;
	}
	public void setUtentiInteressati(List<Utente> utentiInteressati) {
		this.utentiInteressati = utentiInteressati;
	}
	
	/* Il type LAZY è per dire "esegui quando viene richiesto"
	 * Il type EAGER è per dire "esegui subito a prescindere"
	 */
	
	@ManyToMany(fetch=FetchType.LAZY, targetEntity=Categoria.class)
	@JoinTable(name = "Tags", 
		joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id_post"), 
		inverseJoinColumns = @JoinColumn(name = "categoria_id", referencedColumnName = "id_categoria"))
	@JsonIgnoreProperties("postAppartenenti")
	@JsonView(postListaCategorie.class)
	public List<Categoria> getTagPost() {
		return tagPost;
	}
	public void setTagPost(List<Categoria> tagPost) {
		this.tagPost = tagPost;
	}
	
	
	/* Qui siamo nela parte "ONE" che si teorizza possiede più elementi 
	 * quindi qui avremo sempre una lista, un set, un insieme di oggetti
	 * dell'altra entità
	 * 
	 * Inoltre qui usiamo solo "mappedBy" per referenziare la proprietà
	 * di riferimento sull'altra entità
	 * 
	 * Questa è definita "PARENT CLASS" mentre l'altra è la "CHILD CLASS"
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy="post_id")
	@JsonView(postListaImmagini.class)
	public List<Immagine> getImmaginiAssociate() {
		return immaginiAssociate;
	}
	public void setImmaginiAssociate(List<Immagine> immaginiAssociate) {
		this.immaginiAssociate = immaginiAssociate;
	}
	
	public Post() {}
	
	
	public Post(Date data, String titolo, String contenuto, Utente utente_id) {
		this.data = data;
		this.titolo = titolo;
		this.contenuto = contenuto;
		this.utente_id = utente_id;
	}
	
	
	@Override
	public String toString() {
		return "Post [id_post=" + id_post + ", data=" + data + ", titolo=" + titolo + ", contenuto=" + contenuto
				+ ", utente_id=" + utente_id + "]";
	}
	    
}

