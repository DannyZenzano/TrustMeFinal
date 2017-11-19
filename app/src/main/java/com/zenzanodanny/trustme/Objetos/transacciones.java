package com.zenzanodanny.trustme.Objetos;

/**
 * Created by Zenzano on 6/11/2017.
 */

public class transacciones {
    String tr_id_vendedor;
    String tr_id_comprador;
    String tr_estado_transaccion;
    String tr_titulo;
    String tr_descripcion;
    String tr_estado_conservacion;
    String tr_fotografia;
    String tr_fecha_entrega;
    String tr_lugar_entrega;
    String tr_coordenadas;
    String tr_fecha_registro;

    public String getTr_id_vendedor() {
        return tr_id_vendedor;
    }

    public void setTr_id_vendedor(String tr_id_vendedor) {
        this.tr_id_vendedor = tr_id_vendedor;
    }

    public String getTr_id_comprador() {
        return tr_id_comprador;
    }

    public void setTr_id_comprador(String tr_id_comprador) {
        this.tr_id_comprador = tr_id_comprador;
    }

    public String getTr_estado_transaccion() {
        return tr_estado_transaccion;
    }

    public void setTr_estado_transaccion(String tr_estado_transaccion) {
        this.tr_estado_transaccion = tr_estado_transaccion;
    }

    public String getTr_titulo() {
        return tr_titulo;
    }

    public void setTr_titulo(String tr_titulo) {
        this.tr_titulo = tr_titulo;
    }

    public String getTr_descripcion() {
        return tr_descripcion;
    }

    public void setTr_descripcion(String tr_descripcion) {
        this.tr_descripcion = tr_descripcion;
    }

    public String getTr_estado_conservacion() {
        return tr_estado_conservacion;
    }

    public void setTr_estado_conservacion(String tr_estado_conservacion) {
        this.tr_estado_conservacion = tr_estado_conservacion;
    }


    public String getTr_fotografia() {
        return tr_fotografia;
    }

    public void setTr_fotografia(String tr_fotografia) {
        this.tr_fotografia = tr_fotografia;
    }

    public String getTr_fecha_entrega() {
        return tr_fecha_entrega;
    }

    public void setTr_fecha_entrega(String tr_fecha_entrega) {
        this.tr_fecha_entrega = tr_fecha_entrega;
    }

    public String getTr_lugar_entrega() {
        return tr_lugar_entrega;
    }

    public void setTr_lugar_entrega(String tr_lugar_entrega) {
        this.tr_lugar_entrega = tr_lugar_entrega;
    }

    public String getTr_coordenadas() {
        return tr_coordenadas;
    }

    public void setTr_coordenadas(String tr_coordenadas) {
        this.tr_coordenadas = tr_coordenadas;
    }

    public String getTr_fecha_registro() {
        return tr_fecha_registro;
    }

    public void setTr_fecha_registro(String tr_fecha_registro) {
        this.tr_fecha_registro = tr_fecha_registro;
    }

    public transacciones(String tr_id_vendedor, String tr_id_comprador, String tr_estado_transaccion, String tr_titulo, String tr_descripcion, String tr_estado_conservacion, String tr_fotografia, String tr_fecha_entrega, String tr_lugar_entrega, String tr_coordenadas, String tr_fecha_registro) {
        this.tr_id_vendedor = tr_id_vendedor;
        this.tr_id_comprador = tr_id_comprador;
        this.tr_estado_transaccion = tr_estado_transaccion;

        this.tr_titulo = tr_titulo;
        this.tr_descripcion = tr_descripcion;
        this.tr_estado_conservacion = tr_estado_conservacion;
        this.tr_fotografia = tr_fotografia;
        this.tr_fecha_entrega = tr_fecha_entrega;
        this.tr_lugar_entrega = tr_lugar_entrega;
        this.tr_coordenadas = tr_coordenadas;
        this.tr_fecha_registro = tr_fecha_registro;
    }

    public transacciones() {
    }

    @Override
    public String toString() {
        return "transacciones{" +
                "tr_id_vendedor='" + tr_id_vendedor + '\'' +
                ", tr_id_comprador='" + tr_id_comprador + '\'' +
                ", tr_estado_transaccion='" + tr_estado_transaccion + '\'' +
                ", tr_titulo='" + tr_titulo + '\'' +
                ", tr_descripcion='" + tr_descripcion + '\'' +
                ", tr_estado_conservacion='" + tr_estado_conservacion + '\'' +
                ", tr_fotografia='" + tr_fotografia + '\'' +
                ", tr_fecha_entrega='" + tr_fecha_entrega + '\'' +
                ", tr_lugar_entrega='" + tr_lugar_entrega + '\'' +
                ", tr_coordenadas='" + tr_coordenadas + '\'' +
                ", tr_fecha_registro='" + tr_fecha_registro + '\'' +
                '}';
    }
}
