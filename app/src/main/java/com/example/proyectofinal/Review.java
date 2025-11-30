package com.example.proyectofinal;

public class Review {
    private String comentario;
    private float rating;
    private String fotoUri;

    public Review(String comentario, float rating, String fotoUri) {
        this.comentario = comentario;
        this.rating = rating;
        this.fotoUri = fotoUri;
    }

    public String getComentario() { return comentario; }
    public float getRating() { return rating; }
    public String getFotoUri() { return fotoUri; }
}