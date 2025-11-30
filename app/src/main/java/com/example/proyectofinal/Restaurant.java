package com.example.proyectofinal;

public class Restaurant {
    private int id;
    private String name;
    private String type;
    private double lat;
    private double lng;

    // ESTE ES EL CAMPO QUE FALTABA
    private String distanciaStr;

    public Restaurant(int id, String name, String type, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
        this.distanciaStr = ""; // Inicializar vacío para evitar nulos
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }

    // ESTOS SON LOS MÉTODOS QUE FALTABAN
    public String getDistanciaStr() {
        return distanciaStr;
    }

    public void setDistanciaStr(String distanciaStr) {
        this.distanciaStr = distanciaStr;
    }
}