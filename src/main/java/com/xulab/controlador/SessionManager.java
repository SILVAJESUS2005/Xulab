package com.xulab.controlador;

import com.xulab.modelo.Usuario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 * Este bean gestiona la sesión del usuario. Guarda la información del usuario
 * que ha iniciado sesión.
 * @author jesus
 */
@Named("sessionManager")
@SessionScoped
public class SessionManager implements Serializable {

    private Usuario usuarioLogueado;

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    /**
     * Comprueba si hay un usuario en la sesión.
     *
     * @return true si el usuario ha iniciado sesión, false en caso contrario.
     */
    public boolean isLoggedIn() {
        return usuarioLogueado != null;
    }

}
