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

    // 2. Creamos los objetos que contendrán los datos para la vista
    private Leccion leccionActual;
    private List<Comentario> comentarios;
    private int leccionId; // Para guardar el ID de la URL

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

// 5. Creamos los Getters para que la página JSF pueda acceder a los datos
    public Leccion getLeccionActual() {
        return leccionActual;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }
        /**
     * Convierte una URL estándar de YouTube a una URL para ser incrustada en un iframe.
     * @return La URL modificada o una cadena vacía si no hay lección.
     */
public String getYouTubeEmbedUrl() {
    if (leccionActual == null || leccionActual.getVideoUrl() == null || leccionActual.getVideoUrl().isEmpty()) {
        return ""; // No hay URL, no mostramos nada.
    }

    String videoUrl = leccionActual.getVideoUrl();

    // Comprobamos si es una URL estándar de YouTube que contiene "watch?v="
    if (videoUrl.contains("watch?v=")) {
        String[] parts = videoUrl.split("v=");
        if (parts.length > 1) {
            // Extraemos la parte que sigue a "v="
            String videoId = parts[1];
            // A veces la URL puede tener más parámetros (ej. &t=10s), los quitamos.
            int ampersandPosition = videoId.indexOf('&');
            if (ampersandPosition != -1) {
                videoId = videoId.substring(0, ampersandPosition);
            }
            return "https://www.youtube.com/embed/" + videoId;
        }
    }
    
    // Opcional: Podríamos añadir lógica para otros formatos como youtu.be

    // Si la URL no tiene el formato esperado, devolvemos una cadena vacía para no romper la página.
    System.err.println("URL de video no válida: " + videoUrl);
    return "";
}

}
