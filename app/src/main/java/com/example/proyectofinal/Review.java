package com.example.proyectofinal;

public class Review {
    private int id; // <--- NUEVO CAMPO IMPORTANTE
    private String comentario;
    private float rating;
    private String fotoUri;

    public Review(int id, String comentario, float rating, String fotoUri) {
        this.id = id;
        this.comentario = comentario;
        this.rating = rating;
        this.fotoUri = fotoUri;
    }

    public int getId() { return id; } // Getter del ID
    public String getComentario() { return comentario; }
    public float getRating() { return rating; }
    public String getFotoUri() { return fotoUri; }
}