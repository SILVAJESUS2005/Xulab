/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xulab.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author jesus
 */
// Esta clase es un "espejo" de tu tabla cursos en Java.
// @Entity le dice al JPA que esta clase representa una tabla en la BD.
@Entity
// @Table especifica el nombre exacto de la tabla en la BD.
@Table(name = "cursos")
public class Curso implements Serializable {

    // @ID indica que este atributo es la clave primaria (Primary Key).
    @Id
    // @GeneratedValue le dice a JPA como se genera esa clave(en este caso, auto-incremental por la BD).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // @Column se usa para mapear el atributo a una columna específica. Si se llaman igual es opcional.
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "video_intro_url")
    private String videoIntroUrl;
    // --- Getters y Setters ---
    // Son necesarios para que JSF Y JPA puedan acceder a los atributos

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getVideoIntroUrl() {
        return videoIntroUrl;
    }

    public void setVideoIntroUrl(String videoIntroUrl) {
        this.videoIntroUrl = videoIntroUrl;
    }

}
