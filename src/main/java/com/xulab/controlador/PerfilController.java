package com.xulab.controlador;


import com.xulab.controlador.SessionManager;
import com.xulab.dao.InscripcionDAO;
import com.xulab.modelo.Inscripcion;
import com.xulab.modelo.Usuario;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
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

@Named("perfilController")
@ViewScoped
public class PerfilController implements Serializable {
    // Inyecciones: identificar quien esta logueado
    @Inject
    private SessionManager sessionManager;
    
    @Inject
    private InscripcionDAO inscripcionDAO;
    
    // Variables que almacenan los datos que mostrara la vista
    private Usuario usuario;
    private List<Inscripcion> inscripciones;
    
    // Ejecutar automáticamente al cargar la página
    @PostConstruct
    public void init(){
        // Verificar por seguridad que alguien este logueado
        if (sessionManager.isLoggedIn()) {
            // Obtener el usuario actual de la sesión
            this.usuario = sessionManager.getUsuarioLogueado();
            
            // Usamos el método de InscripcionesDAO
            // Buscamos todos los cursos a los que esta inscrito el usuario
            this.inscripciones = inscripcionDAO.buscarPorUsuario(this.usuario);
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }
    
    
}
