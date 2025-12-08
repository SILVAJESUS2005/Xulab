package com.xulab.controlador;

import com.xulab.dao.CursoDAO;
import com.xulab.modelo.Curso;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Controlador JSF encargado de la gestión y visualización del catálogo de cursos.
 * Este Managed Bean tiene un alcance de vista, ideal para mantener
 * la lista de cursos cargada mientras el usuario navega en la página principal de cursos.
 * Su principal responsabilidad es obtener la oferta educativa disponible y 
 * presentarla en la interfaz de usuario.
 * @author jesus
 */
// @Named hace que esta clase sea un CDI Bean, accesible desde las páginas JSF.
// Es el estándar moderno, reemplazando a @ManagedBean.
@Named(value = "cursoController")
// @ViewScoped significa que este objeto vivirá mientras estés en la misma página.
// Si navegas a otra página y vuelves, se creará de nuevo. Es ideal para listas.
@ViewScoped
public class CursoController implements Serializable {

    private List<Curso> cursos;

    // @PostConstruct es una anotación muy útil. El método maarcado con ella
    // se ejecutará automáticamente después de que el controlador sea creado.
    //Es el lugar para inicializar datos.
    @Inject
    private CursoDAO cursoDAO;

    @PostConstruct
    public void init() {
        this.cursos = cursoDAO.listarTodos();
    }

    public List<Curso> getCursos() {
        return cursos;
    }

}
