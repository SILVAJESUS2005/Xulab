package com.xulab.controlador;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.xulab.dao.CursoDAO;
import com.xulab.dao.InscripcionDAO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 *
 * @author jesus
 */
@Named("nosotrosController")
@ViewScoped
public class NosotrosController implements Serializable {

    @Inject
    private CursoDAO cursoDAO;

    @Inject
    private InscripcionDAO inscripcionDAO;

    private long totalCursos;
    private long totalAlumnos;

    @PostConstruct
    public void init() {
        this.totalCursos = cursoDAO.contarCursos();
        this.totalAlumnos = inscripcionDAO.contarInscripciones();
    }

    public long getTotalCursos() {
        return totalCursos;
    }

    public long getTotalAlumnos() {
        return totalAlumnos;
    }
}
