/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xulab.controlador;

import com.xulab.dao.ModuloDAO;
import com.xulab.modelo.Leccion;
import com.xulab.modelo.Modulo;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author danie
 */

@Named(value="contenidoController")
@ViewScoped
public class ContenidoCursoController implements Serializable{
    
        // Inyecta el DAO
    @Inject
    private ModuloDAO moduloDAO;
    
    private List<Modulo> modulos;

    @PostConstruct
    public void init() {
        
        
            // Obtenemos el mapa de parámetros de la URL
    String idParam = FacesContext.getCurrentInstance().getExternalContext()
                        .getRequestParameterMap().get("cursoId");
    
    // Verificamos que el parámetro no sea nulo
    if (idParam != null && !idParam.isEmpty()) {
        try {
            int idCurso = Integer.parseInt(idParam);
            // Usamos el ID REAL de la URL para buscar en la base de datos
            this.modulos = moduloDAO.buscarPorCursoId(idCurso);
        } catch (NumberFormatException e) {
            // Manejar el caso de que el ID no sea un número válido
            System.err.println("Error: El ID del curso no es un número válido.");
        }
    }
    }
//        // Aquí simulamos que queremos ver el curso con ID = 1.
//        // Más adelante, aprenderemos a pasar este ID desde la página anterior.
//        int idCursoPrueba = 1; 
//        
//        // Llama al DAO para obtener los datos reales
//        this.modulos = moduloDAO.buscarPorCursoId(idCursoPrueba);
//    }


//    private List <Modulo> modulos;
//    @PostConstruct
//    public void init (){
//        modulos = new ArrayList <> ();
//        Modulo m1 = new Modulo();
//        m1.setNombre("Tema 1: Fundamentos de Phyton");
//        List<Leccion> leccionesM1 = new ArrayList<>();
//        Leccion l11 = new Leccion(); l11.setNombre("1.1 Introducción a Python");
//        Leccion l12 = new Leccion(); l12.setNombre("1.2 Variables y Tipos de Datos");
//        leccionesM1.add(l11);
//        leccionesM1.add(l12);
//        m1.setLecciones(leccionesM1);
//
//        // Módulo 2 con sus lecciones
//        Modulo m2 = new Modulo();
//        m2.setNombre("Tema 2: Control de Flujo");
//        List<Leccion> leccionesM2 = new ArrayList<>();
//        Leccion l21 = new Leccion(); l21.setNombre("2.1 Condicionales if/else");
//        Leccion l22 = new Leccion(); l22.setNombre("2.2 Bucles for y while");
//        leccionesM2.add(l21);
//        leccionesM2.add(l22);
//        m2.setLecciones(leccionesM2);
//
//        modulos.add(m1);
//        modulos.add(m2);
//        
//    }
//    
//    public List<Modulo> getModulos() {
//        return modulos;
//    }
        public List<Modulo> getModulos() {
        return modulos;
    }
}

