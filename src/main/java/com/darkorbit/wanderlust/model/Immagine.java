package com.darkorbit.wanderlust.model;

import java.io.Serializable;

import com.darkorbit.wanderlust.config.JsonViews.postListaImmagini;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.*;

@SuppressWarnings("serial")
@Entity(name="Immagini")
@Table(name = "Immagini")
public class Immagine implements Serializable{
	private long id_immagine;
	private String url;
	private Post post_id;
  
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(postListaImmagini.class)
	public long getId_immagine() {
		return id_immagine;
	}

	public void setId_immagine(long id_immagine) {
		this.id_immagine = id_immagine;
	}
	
	@JsonView(postListaImmagini.class)
	@Column(name = "url", nullable = true)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	/* Di nuovo nella parte "MANY" definiamo la JoinColumn, quindi il nome
	 * "owning side"
	 * Altro dettaglio è che nella parte "MANY" avremo sempre il riferimento
	 * ad un singolo oggetto della classe "ONE" in questo caso un Post
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	@JsonIgnore
	public Post getPost_id() {
		return post_id;
	}
	public void setPost_id(Post post_id) {
		this.post_id = post_id;
	}
	
	public Immagine() {}
	
	public Immagine(String url, Post post_id) {
		this.url = url;
		this.post_id = post_id;
	}
	
	@Override
	public String toString() {
		return "Immagine [id_immagine=" + id_immagine + ", url=" + url + ", post_id=" + post_id + "]";
	}
}
