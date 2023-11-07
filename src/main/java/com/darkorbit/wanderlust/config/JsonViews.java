package com.darkorbit.wanderlust.config;

public class JsonViews {
	
	public interface utenteView{}
	public interface utenteListaArticoli extends utenteView {}
	public interface utenteListaPreferiti extends utenteView {}
	
	public interface postView {}
	public interface postAutore extends postView {}
	public interface postListaCategorie extends postView {}
	public interface postListaImmagini extends postView {}
	
	public interface categoriaView {}
	public interface categoriaListaPost extends categoriaView {}
	
	public interface immagineView {}
	public interface immagineListaPost extends immagineView {}


}
