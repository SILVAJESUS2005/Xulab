package com.xulab.modelo;

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

/**
 * Representa un módulo o sección dentro de un curso. Esta entidad actúa como un
 * contenedor intermedio que agrupa varias lecciones y pertenece a un curso
 * específico. Mapea la tabla 'modulos' en la base de datos.
 *
 * @author jesus
 */
@Entity
@Table(name = "modulos")
public class Modulo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;

    // Relación: Muchos módulos pertenecen a un curso
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    // Relación: Un módulo tiene muchas lecciones
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

    /**
     * Obtiene la lista de lecciones asociadas a este módulo. 
     *
     * @return Lista de objetos leccion.
     */
    public List<Leccion> getLecciones() {
        return lecciones;
    }

    public void setLecciones(List<Leccion> lecciones) {
        this.lecciones = lecciones;
    }

}
