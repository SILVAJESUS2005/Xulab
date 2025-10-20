/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xulab.controlador;

import com.xulab.dao.ComentarioDAO;
import com.xulab.dao.LeccionDAO;
import com.xulab.modelo.Comentario;
import com.xulab.modelo.Leccion;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import com.xulab.modelo.Usuario;
import java.util.Date;

/**
 *
 * @author jesus
 */

@Named("leccionController")
@ViewScoped
public class LeccionController implements Serializable {
    // 1. Inyectamos los DAOs que ya creamos

    @Inject
    private LeccionDAO leccionDAO;

    @Inject
    private ComentarioDAO comentarioDAO;
    
    @Inject 
    private SessionManager sessionManager;

    // 2. Creamos los objetos que contendrán los datos para la vista
    private Leccion leccionActual;
    private List<Comentario> comentarios;
    private int leccionId; // Para guardar el ID de la URL
    private String nuevoComentarioTexto;
    
    @PostConstruct
    public void init() {
        // 3. Obtenemos el parámetro 'leccionId' de la URL
        String idParam = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("leccionId");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                this.leccionId = Integer.parseInt(idParam);

                // 4. Usamos los DAOs para cargar los datos
                this.leccionActual = leccionDAO.buscarPorId(this.leccionId);
                this.comentarios = comentarioDAO.buscarPorLeccionId(this.leccionId);

            } catch (NumberFormatException e) {
                // Manejo de error si el ID no es un número válido
                System.err.println("Error: El ID de la lección no es un número válido.");
            }
        }
    }
    // Método para guardar los comentarios
    public void agregarComentario() {
        // Verifica que haya una sesión iniciada y que el texto del comentario no este vacío
        if(sessionManager.isLoggedIn() && nuevoComentarioTexto != null && !nuevoComentarioTexto.trim().isEmpty()) {
            // Creamos el objeto comentario
            Comentario comentario = new Comentario();
            
            // Asignar propiedades
            comentario.setTexto(nuevoComentarioTexto); // Comentario
            comentario.setFechaCreacion(new Date()); // Fecha y hora
            comentario.setLeccion(this.leccionActual); // La lección en la que se encuentra
            comentario.setAutor(sessionManager.getUsuarioLogueado()); // El usuario que hace el comentario
            
            // Se usa el DAO para guardar en la BD
            comentarioDAO.crear(comentario);
            
            // Volvemos a cargar la lista de comentarios para que el nuevo aparezca 
            this.comentarios = comentarioDAO.buscarPorLeccionId(this.leccionId);
            
            // Limpias campo de texto para un nuevo comentario
            this.nuevoComentarioTexto = "";
        }
    }

// 5. Creamos los Getters para que la página JSF pueda acceder a los datos
    public Leccion getLeccionActual() {
        return leccionActual;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public String getNuevoComentarioTexto() {
        return nuevoComentarioTexto;
    }

    public void setNuevoComentarioTexto(String nuevoComentarioTexto) {
        this.nuevoComentarioTexto = nuevoComentarioTexto;
    }
  
}
