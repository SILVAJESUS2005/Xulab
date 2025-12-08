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
import java.util.Properties;
import java.util.UUID;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador para manejar la lógica de registro e inicio de sesión.
 *
 * @author jesus
 */
@Named("authController")
@RequestScoped // La información (nombre, email, etc.) solo se necesita para una petición.
public class AuthController {

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
        // 1. Verificamos si existe
        if (usuarioDAO.buscarPorEmail(email) != null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El email ya está registrado."));
            return null;
        }

        // Creamos un nuevo objeto Usuario con los datos del formulario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);

        // Hasheamos la contraseña antes de guardarla 
        String hashedPassword = passwordHasher.generate(password.toCharArray());
        nuevoUsuario.setPassword(hashedPassword);

        // Generar Token y marcar como NO verificado
        String token = UUID.randomUUID().toString(); // Generar token
        nuevoUsuario.setTokenVerificacion(token);
        nuevoUsuario.setVerificado(false); // Nace sin verificar

        // Usamos el DAO para guardar el nuevo usuario en la BD
        usuarioDAO.crear(nuevoUsuario);

        // Enviar el correo de activación
        enviarCorreoActivacion(email, nombre, token);

        System.out.println("!Usuario registrado con éxito!");
        //Enviamos una señal 'registroExitoso' al frontend.
        PrimeFaces.current().ajax().addCallbackParam("registroExitoso", true);
        return null;
    }

    /**
     * Lógica para verificar las credenciales e iniciar sesión.
     *
     * @return La página a la que redirigir tras el inicio de sesión.
     */
    public String login() {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        if (usuario != null && passwordHasher.verify(password.toCharArray(), usuario.getPassword())) {

            // NUEVA VALIDACIÓN: ¿Está verificado?
            if (!usuario.isVerificado()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Cuenta no verificada", "Por favor revisa tu correo y activa tu cuenta."));
                return null;
            }

            sessionManager.setUsuarioLogueado(usuario);
            return "/cursos?faces-redirect=true"; // Usamos ruta absoluta /cursos
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Credenciales incorrectas."));
            return null;
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     *
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

    private void enviarCorreoActivacion(String destinatario, String nombreUsuario, String token) {
        // Tu configuración de Gmail (La misma que usaste en AyudaController)
        String remitente = "xulab.educacion@gmail.com";
        String passwordApp = "rxlhyvviztloldvc";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Seguridad extra
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, passwordApp);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Activa tu cuenta en Xulab");

            // Creamos el enlace de activación
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

            String urlBase = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

            String link = urlBase + "/activar.xhtml?token=" + token;

            // Mensaje en HTML para que se vea bonito
            String contenidoHtml = "<h1>¡Hola " + nombreUsuario + "!</h1>"
                    + "<p>Gracias por unirte a Xulab. Para comenzar a aprender, por favor confirma tu correo electrónico.</p>"
                    + "<p><a href='" + link + "' style='background-color: #00A884; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Activar mi Cuenta</a></p>"
                    + "<p>O copia y pega este enlace: " + link + "</p>";

            message.setContent(contenidoHtml, "text/html; charset=utf-8");

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            // No detenemos el registro si falla el correo, pero lo logueamos
            System.err.println("Error enviando correo de activación: " + e.getMessage());
        }
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
