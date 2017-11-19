package com.zenzanodanny.trustme.Objetos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zenzano on 1/11/2017.
 */

public class Usuario {
    String user_msisdn;
    String user_country_code;
    String user_state;
    String user_registerdate;
    String user_name;
    String user_lastname;
    String user_photo;
    int user_num_ventas;
    int user_num_compras;
    int user_prom_calif_ventas;
    int user_prom_calif_compras;
    int user_nro_ventas_incumplidas;
    int user_nro_compras_incumplidas;

    public Usuario() {
    }

    //COnstructor para REGISTRO.
    public Usuario(String user_msisdn, String user_country_code) {
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        //Datos Recibidos
        this.user_msisdn = user_msisdn;
        this.user_country_code=user_country_code;
        //Datos por defecto
        this.user_state="activo";
        this.user_registerdate=hourdateFormat.format(date);
        this.user_name ="";
        this.user_lastname ="";
        this.user_photo="Default";
        this.user_num_ventas = 0;
        this.user_num_compras = 0;
        this.user_prom_calif_ventas = 0;
        this.user_prom_calif_compras = 0;
        this.user_nro_ventas_incumplidas = 0;
        this.user_nro_compras_incumplidas = 0;
    }


    public Usuario(String user_msisdn, String user_country_code, String user_state, String user_registerdate, String user_name, String user_lastname, String user_photo, int user_num_ventas, int user_num_compras, int user_prom_calif_ventas, int user_prom_calif_compras, int user_nro_ventas_incumplidas, int user_nro_compras_incumplidas) {
        this.user_msisdn = user_msisdn;
        this.user_country_code = user_country_code;
        this.user_state = user_state;
        this.user_registerdate = user_registerdate;
        this.user_name = user_name;
        this.user_lastname = user_lastname;
        this.user_photo = user_photo;
        this.user_num_ventas = user_num_ventas;
        this.user_num_compras = user_num_compras;
        this.user_prom_calif_ventas = user_prom_calif_ventas;
        this.user_prom_calif_compras = user_prom_calif_compras;
        this.user_nro_ventas_incumplidas = user_nro_ventas_incumplidas;
        this.user_nro_compras_incumplidas = user_nro_compras_incumplidas;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "user_msisdn='" + user_msisdn + '\'' +
                ", user_country_code='" + user_country_code + '\'' +
                ", user_state='" + user_state + '\'' +
                ", user_registerdate='" + user_registerdate + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_lastname='" + user_lastname + '\'' +
                ", user_photo='" + user_photo + '\'' +
                ", user_num_ventas=" + user_num_ventas +
                ", user_num_compras=" + user_num_compras +
                ", user_prom_calif_ventas=" + user_prom_calif_ventas +
                ", user_prom_calif_compras=" + user_prom_calif_compras +
                ", user_nro_ventas_incumplidas=" + user_nro_ventas_incumplidas +
                ", user_nro_compras_incumplidas=" + user_nro_compras_incumplidas +
                '}';
    }

    public String getUser_msisdn() {
        return user_msisdn;
    }

    public void setUser_msisdn(String user_msisdn) {
        this.user_msisdn = user_msisdn;
    }

    public String getUser_country_code() {
        return user_country_code;
    }

    public void setUser_country_code(String user_country_code) {
        this.user_country_code = user_country_code;
    }

    public String getUser_state() {
        return user_state;
    }

    public void setUser_state(String user_state) {
        this.user_state = user_state;
    }

    public String getUser_registerdate() {
        return user_registerdate;
    }

    public void setUser_registerdate(String user_registerdate) {
        this.user_registerdate = user_registerdate;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_lastname() {
        return user_lastname;
    }

    public void setUser_lastname(String user_lastname) {
        this.user_lastname = user_lastname;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public int getUser_num_ventas() {
        return user_num_ventas;
    }

    public void setUser_num_ventas(int user_num_ventas) {
        this.user_num_ventas = user_num_ventas;
    }

    public int getUser_num_compras() {
        return user_num_compras;
    }

    public void setUser_num_compras(int user_num_compras) {
        this.user_num_compras = user_num_compras;
    }

    public int getUser_prom_calif_ventas() {
        return user_prom_calif_ventas;
    }

    public void setUser_prom_calif_ventas(int user_prom_calif_ventas) {
        this.user_prom_calif_ventas = user_prom_calif_ventas;
    }

    public int getUser_prom_calif_compras() {
        return user_prom_calif_compras;
    }

    public void setUser_prom_calif_compras(int user_prom_calif_compras) {
        this.user_prom_calif_compras = user_prom_calif_compras;
    }

    public int getUser_nro_ventas_incumplidas() {
        return user_nro_ventas_incumplidas;
    }

    public void setUser_nro_ventas_incumplidas(int user_nro_ventas_incumplidas) {
        this.user_nro_ventas_incumplidas = user_nro_ventas_incumplidas;
    }

    public int getUser_nro_compras_incumplidas() {
        return user_nro_compras_incumplidas;
    }

    public void setUser_nro_compras_incumplidas(int user_nro_compras_incumplidas) {
        this.user_nro_compras_incumplidas = user_nro_compras_incumplidas;
    }
}
