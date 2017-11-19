package com.zenzanodanny.trustme.Objetos;

/**
 * Created by Zenzano on 18/11/2017.
 */

public class rating {
    float articulo;
    float comunicacion;
    float puntualidad;
    String idComprador;
    String idVendedor;


    public rating() {
    }

    public rating(float articulo, float comunicacion, float puntualidad, String idComprador, String idVendedor) {
        this.articulo = articulo;
        this.comunicacion = comunicacion;
        this.puntualidad = puntualidad;
        this.idComprador = idComprador;
        this.idVendedor = idVendedor;
    }

    @Override
    public String toString() {
        return "rating{" +
                "articulo=" + articulo +
                ", comunicacion=" + comunicacion +
                ", puntualidad=" + puntualidad +
                ", idComprador='" + idComprador + '\'' +
                ", idVendedor='" + idVendedor + '\'' +
                '}';
    }

    public float getArticulo() {
        return articulo;
    }

    public void setArticulo(float articulo) {
        this.articulo = articulo;
    }

    public float getComunicacion() {
        return comunicacion;
    }

    public void setComunicacion(float comunicacion) {
        this.comunicacion = comunicacion;
    }

    public float getPuntualidad() {
        return puntualidad;
    }

    public void setPuntualidad(float puntualidad) {
        this.puntualidad = puntualidad;
    }

    public String getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(String idComprador) {
        this.idComprador = idComprador;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }
}
