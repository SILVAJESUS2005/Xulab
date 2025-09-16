/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xulab.controlador;

import com.xulab.dao.CursoDAO;
import com.xulab.modelo.Curso;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jesus
 */
// @Named hace que esta clase sea un CDI Bean, accesible desde las páginas JSF.
// Es el estándar moderno, reemplazando a @ManagedBean.
@Named(value = "cursoController")
// @ViewScoped significa que este objeto vivirá mientras estés en la misma página.
// Si navegas a otra página y vuelves, se creará de nuevo. Es ideal para listas.
@ViewScoped
public class CursoController implements Serializable {

    // Esta sera la lista de cursos que se mostraran en la página.
    private List<Curso> cursos;

    // @PostConstruct es una anotación muy útil. El método maarcado con ella
    // se ejecutará automáticamente después de que el controlador sea creado.
    //Es el lugar para inicializar datos.
    @Inject
    private CursoDAO cursoDAO;

    @PostConstruct
    public void init() {
        
        
    // Esta única línea llama al DAO
    this.cursos = cursoDAO.listarTodos(); 
//        //Creamos una lista de cursos temporal para probar la vista.
//        cursos = new ArrayList<>();
//
//        Curso c1 = new Curso();
//        c1.setId(1);
//        c1.setNombre("Java");
//        c1.setDescripcion("Java es un lenguaje de programación de propósito general, concurrente, basado en clases y orientado a objetos que está específicamente diseñado para tener tan pcoas dependencias de implementación como sea posible.");
//        c1.setImagenUrl("java.png"); // Imagen del curso
//
//        Curso c2 = new Curso();
//        c2.setId(2);
//        c2.setNombre("C#");
//        c2.setDescripcion("C# es un lenguaje de programación multiparadigma que abarca disciplinas como la programación estructurada, orientada a objetos, genérica, funcional, imperativa y declarativa.");
//        c2.setImagenUrl("csharp.png"); // Imagen del curso
//
//        Curso c3 = new Curso();
//        c3.setId(3);
//        c3.setNombre("Habilidades de comunicación");
//        c3.setDescripcion("La formación en habilidades blandas es vital para los trabajos porque mejoran la comunicación, el trabajo en equipo, la resolución de problemas y la adaptabilidad, creando un entorno laboral más positivo y productivo.");
//        c3.setImagenUrl("habilidades.png"); // Imagen del curso
//
//        cursos.add(c1);
//        cursos.add(c2);
//        cursos.add(c3);

    }

    // --- Getter ---
    // La página JSF necesitará un getter para poder acceder a la lista de cursos.
    public List<Curso> getCursos() {
        return cursos;
    }

}
