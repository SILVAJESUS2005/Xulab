package com.xulab.controlador;

import com.xulab.dao.UsuarioDAO;
import com.xulab.modelo.Usuario;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 * Controlador para manejar la lógica de registro e inicio de sesión.
 *
 * @author jesus
 */
@Named
@ViewScoped
public class ActivacionController implements Serializable {

    @Inject
    private UsuarioDAO usuarioDAO;

    private boolean valido;
    private String mensaje;

    @PostConstruct
    public void init() {
        // Leer el token de la URL
        String token = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("token");

        if (token != null && !token.isEmpty()) {
            Usuario usuario = usuarioDAO.buscarPorToken(token);

            if (usuario != null) {
                if (usuario.isVerificado()) {
                    valido = true;
                    mensaje = "Tu cuenta ya estaba activada. ¡Puedes iniciar sesión!";
                } else {
                    // ACTIVARLO
                    usuario.setVerificado(true);
                    // Borramos el token por seguridad (opcional, o lo dejamos para histórico)
                    usuario.setTokenVerificacion(null);
                    usuarioDAO.actualizar(usuario); // Necesitas el método actualizar en DAO

                    valido = true;
                    mensaje = "¡Cuenta activada con éxito! Bienvenido a Xulab.";
                }
            } else {
                valido = false;
                mensaje = "El enlace de activación no es válido o ha expirado.";
            }
        } else {
            valido = false;
            mensaje = "No se proporcionó ningún código de activación.";
        }
    }

    public boolean isValido() {
        return valido;
    }

    public String getMensaje() {
        return mensaje;
    }
}
