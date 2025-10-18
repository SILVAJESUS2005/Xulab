/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xulab.controlador;

import com.xulab.modelo.Usuario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 *
 * @author jesus
 */
/**
 * Este bean gestiona la sesión del usuario. Guarda la información del usuario
 * que ha iniciado sesión.
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
