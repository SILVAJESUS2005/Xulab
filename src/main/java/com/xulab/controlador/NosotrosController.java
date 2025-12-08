package com.xulab.controlador;

import com.xulab.dao.CursoDAO;
import com.xulab.dao.InscripcionDAO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 * Administra el número de cursos y personas que usan Xulab. para mostrarlos en
 * la sección de nosotros el número e irlos contabilizando.
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

    /**
     * Actualiza el numero de cursos que ofrece la plataforma.
     *
     * @return numero de cursos activos en ese momento.
     */
    public long getTotalCursos() {
        return totalCursos;
    }

    /**
     * Actualiza el numero de personas registradas en la plataforma.
     *
     * @return numero de personas registradas en Xulab.
     */
    public long getTotalAlumnos() {
        return totalAlumnos;
    }
}
