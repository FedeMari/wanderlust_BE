package com.darkorbit.wanderlust.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.darkorbit.wanderlust.config.JsonViews.categoriaListaPost;
import com.darkorbit.wanderlust.config.JsonViews.categoriaView;
import com.darkorbit.wanderlust.config.JsonViews.postListaCategorie;
import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;

@SuppressWarnings("serial")
@Entity(name="Categorie")
@Table(name = "Categorie")
public class Categoria implements Serializable{
	private long id_categoria;
	private String nome_categoria;
	
		
	private List<Post> postAppartenenti = new ArrayList<Post>();
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({postListaCategorie.class, categoriaView.class})
	public long getId_categoria() {
		return id_categoria;
	}
	public void setId_categoria(long id_categoria) {
		this.id_categoria = id_categoria;
	}
	
	@JsonView({postListaCategorie.class, categoriaView.class})
	@Column(name = "nome_categoria", unique = true)
	public String getNome_categoria() {
		return nome_categoria;
	}
	public void setNome_categoria(String nome_categoria) {
		this.nome_categoria = nome_categoria;
	}
	
	
	@ManyToMany(mappedBy="tagPost", targetEntity=Post.class)
	@JsonIgnoreProperties({"utentiInteressati", "tagPost", "immaginiAssociate"})
	@JsonView(categoriaListaPost.class)
	public List<Post> getPostAppartenenti() {
		return postAppartenenti;
	}
	public void setPostAppartenenti(List<Post> postAppartenenti) {
		this.postAppartenenti = postAppartenenti;
	}
	
	
	public Categoria() {}
	public Categoria(String nome_categoria) {
		this.nome_categoria = nome_categoria;
	}
	
	@Override
	public String toString() {
		return "Categoria [id_categoria=" + id_categoria + ", nome_categoria=" + nome_categoria + "]";
	}
}
