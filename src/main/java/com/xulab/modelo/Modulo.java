package com.xulab.modelo;


import com.xulab.modelo.Curso;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author jesus
 */
@Entity
@Table(name = "modulos")
public class Modulo implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String nombre;
    
    // Relación: Muchos módulos pertenecen a Un Curso
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;
    
    // Relación: Un módulo tiene Muchas lecciones
    // CascadeType.ALL: Si borras un módulo, se borran sus lecciones.
    // FetchType.EAGER: Carga las lecciones junto con el módulo.
    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Leccion> lecciones;

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

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public List<Leccion> getLecciones() {
        return lecciones;
    }

    public void setLecciones(List<Leccion> lecciones) {
        this.lecciones = lecciones;
    }
    
    
    
}
