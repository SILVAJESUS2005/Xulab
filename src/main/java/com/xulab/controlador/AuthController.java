/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xulab.controlador;

import com.xulab.dao.UsuarioDAO;
import com.xulab.modelo.Usuario;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.primefaces.PrimeFaces;

/**
 *
 * @author jesus
 */
/**
 * Controlador para manejar la lógica de registro e inicio de sesión.
 */
// Cambio en la rama de ejemplo
// Cambio de rama de Nam_chul
@Named("authController")
@RequestScoped // La información (nombre, email, etc.) solo se necesita para una petición.
public class AuthController {

    // Inyectamos las herramientas que se necesitan
    @Inject
    private UsuarioDAO usuarioDAO;

    @Inject
    private Pbkdf2PasswordHash passwordHasher; // El servicio para hashear contraseñas
    
    @Inject
    private SessionManager sessionManager;
    
    // Variables para conectar con los campos del formulario de registro
    private String nombre;
    private String email;
    private String password;

    /**
     * Lógica para registrar un nuevo usuario.
     *
     * @return La página a la que redirigir (ej. login o cursos).
     */
    public String registrar() {
        // Verificamos si existe un usuario con ese email
        if (usuarioDAO.buscarPorEmail(email) != null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de Registro", "El email ya está en uso. Por favor, elige otro."));
            return null; // Nos quedamos en la página para mostrar el error      
        }

        // Creamos un nuevo objeto Usuario con los datos del formulario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);

        // Hasheamos la contraseña antes de guardarla 
        String hashedPassword = passwordHasher.generate(password.toCharArray());
        nuevoUsuario.setPassword(hashedPassword);

        // Usamos el DAO para guardar el nuevo usuario en la BD
        usuarioDAO.crear(nuevoUsuario);

        System.out.println("!Usuario registrado con éxito!");
        //Enviamos una señal 'registroExitoso' al frontend.
        PrimeFaces.current().ajax().addCallbackParam("registroExitoso", true);
        return null;
    }
    
    /**
     * Lógica para verificar las credenciales e iniciar sesión.
     * @return La página a la que redirigir tras el inicio de sesión.
     */
    public String login() {
        // Busca al usuario en la base de datos por email.
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        // Se verifican dos cosas: que el usuario exista y que la contraseña sea correcta.
        if (usuario != null && passwordHasher.verify(password.toCharArray(), usuario.getPassword())) {
            System.out.println("Login exitoso para: " + email);
            // Aquí es dondese guarda al usuario en la sesión para que la aplicación
            // recuerde que ha iniciado sesión. Lo haremos en el siguiente paso.
            // 2. ¡Aquí está la magia! Guardamos el usuario en la sesión.
            sessionManager.setUsuarioLogueado(usuario);
            
            // Redirigimos a la página principal de cursos.
            return "cursos?faces-redirect=true";
        } else {
            // Si el usuario no exixte o la contraseña es incorrecta, mostramos un error.
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de Inicio de Sesión", "Email o contraseña incorrectos."));
            return null; // Nos quedamos en la página de login.
        }
    }
    
    /**
     * Cierra la sesión del usuario actual.
     * @return La página a la que redirigir tras cerrar sesión.
     */
    public String logout() {
        // Obtenemos el contexto externo de JSF.
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        // Invalidamos la sesión actual. Esto borra todos los datos de la sesión.
        ec.invalidateSession();
        System.out.println("Sesión cerrada exitosamente.");
        
        // Redirigimos al usuario a la página de inicio de sesión.
        return "login?faces-redirect=true";
    }

    public UsuarioDAO getUsuarioDAO() {
        return usuarioDAO;
    }

    public void setUsuarioDAO(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Pbkdf2PasswordHash getPasswordHasher() {
        return passwordHasher;
    }

    public void setPasswordHasher(Pbkdf2PasswordHash passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
