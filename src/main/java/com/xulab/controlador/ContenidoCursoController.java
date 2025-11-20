/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xulab.controlador;

import com.xulab.dao.CursoDAO;
import com.xulab.dao.InscripcionDAO;
import com.xulab.dao.ModuloDAO;
import com.xulab.dao.ProgresoDAO;
import com.xulab.modelo.Curso;
import com.xulab.modelo.Inscripcion;
import com.xulab.modelo.Leccion;
import com.xulab.modelo.Modulo;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author danie
 */
@Named(value = "contenidoController")
@ViewScoped
public class ContenidoCursoController implements Serializable {

    // Inyecta el DAO
    @Inject
    private ModuloDAO moduloDAO;

    @Inject
    private InscripcionDAO inscripcionDAO;

    @Inject
    private CursoDAO cursoDAO; // Necesitamos este para obtener el objeto Curso

    @Inject
    private SessionManager sessionManager;

    @Inject
    private ProgresoDAO progresoDAO; // <--- ¡Esta línea es indispensable!

    private Curso cursoActual;
    private List<Modulo> modulos;
    private boolean usuarioInscrito = false; // Por defecto, asumimos que no lo está
    private Set<Integer> leccionesCompletadasIds = new HashSet<>();

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
                this.cursoActual = cursoDAO.buscarPorId(idCurso); // Usamos el CursoDAO
                cargarProgreso(idCurso);
                // 4. Verificamos el estado de la inscripción al cargar la página
                verificarInscripcion();

            } catch (NumberFormatException e) {
                // Manejar el caso de que el ID no sea un número válido
                System.err.println("Error: El ID del curso no es un número válido.");
            }
        }
    }

    private void cargarProgreso(int cursoId) {
        if (sessionManager.isLoggedIn()) {
            // Obtenemos la lista de IDs y la convertimos en un Set para búsqueda rápida
            List<Integer> ids = progresoDAO.obtenerIdsLeccionesCompletadas(sessionManager.getUsuarioLogueado(), cursoId);
            this.leccionesCompletadasIds = new HashSet<>(ids);
        }
    }

    // 1. Verifica si una lección individual está completa (Para el ✅)
    public boolean isLeccionCompletada(int leccionId) {
        return leccionesCompletadasIds.contains(leccionId);
    }

    // 2. Calcula el texto "X/Y Finalizado" para el encabezado del módulo
    public String obtenerProgresoModulo(Modulo modulo) {
        long total = modulo.getLecciones().size();
        long completadas = modulo.getLecciones().stream()
                .filter(l -> leccionesCompletadasIds.contains(l.getId()))
                .count();
        return completadas + "/" + total + " Lecciones Finalizado";
    }

    // 3. Determina si el círculo del módulo debe estar verde (completo) o vacío
    public boolean isModuloCompleto(Modulo modulo) {
        if (modulo.getLecciones().isEmpty()) {
            return false;
        }
        return modulo.getLecciones().stream()
                .allMatch(l -> leccionesCompletadasIds.contains(l.getId()));
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
    // 5. Nuevo método para verificar si el usuario está inscrito
    private void verificarInscripcion() {
        if (sessionManager.isLoggedIn() && cursoActual != null) {
            Inscripcion inscripcion = inscripcionDAO.buscarPorUsuarioYCurso(sessionManager.getUsuarioLogueado(), cursoActual);
            this.usuarioInscrito = (inscripcion != null);
        }
    }

    // 6. Nuevo método para realizar la inscripción
    public void inscribirUsuario() {
        if (sessionManager.isLoggedIn() && cursoActual != null && !usuarioInscrito) {
            Inscripcion nuevaInscripcion = new Inscripcion();
            nuevaInscripcion.setUsuario(sessionManager.getUsuarioLogueado());
            nuevaInscripcion.setCurso(cursoActual);

            inscripcionDAO.crear(nuevaInscripcion);

            // Actualizamos el estado para reflejar el cambio inmediatamente
            this.usuarioInscrito = true;
        }
    }

    /**
     * Calcula el porcentaje total de avance en el curso (0 a 100).
     */
    public int obtenerPorcentajeGeneral() {
        if (modulos == null || modulos.isEmpty()) {
            return 0;
        }

        long totalLecciones = 0;
        long leccionesCompletadas = 0;

        // Recorremos todos los módulos y sus lecciones
        for (Modulo m : modulos) {
            if (m.getLecciones() != null) {
                totalLecciones += m.getLecciones().size();

                for (com.xulab.modelo.Leccion l : m.getLecciones()) {
                    if (leccionesCompletadasIds.contains(l.getId())) {
                        leccionesCompletadas++;
                    }
                }
            }
        }

        if (totalLecciones == 0) {
            return 0;
        }

        // Regla de tres simple para obtener el porcentaje entero
        return (int) ((leccionesCompletadas * 100) / totalLecciones);
    }

    public Curso getCursoActual() {
        return cursoActual;
    }

    public void setCursoActual(Curso cursoActual) {
        this.cursoActual = cursoActual;
    }

    public boolean isUsuarioInscrito() {
        return usuarioInscrito;
    }

    public List<Modulo> getModulos() {
        return modulos;
    }
}
