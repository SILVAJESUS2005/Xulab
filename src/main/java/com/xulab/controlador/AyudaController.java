package com.xulab.controlador;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Controlador JSF encargado de la gestión y visualización del la vista de ayuda.
 * Este Managed Bean tiene un alcance de vista, ideal para mantener
 * la lista de ruta de aprendizaje, preguntas frecuentes, soporte tecnico.
 * @author jesus
 */
@Named
@ViewScoped
public class AyudaController implements Serializable {

    @Inject
    private com.xulab.dao.CursoDAO cursoDAO; // Asegúrate de importar CursoDAO

    // Soporte técnico
    private String asunto;
    private String correoContacto;
    private String descripcion;

    public void enviarReporte() {
        // Configuración del servidor SMTP de Gmail
        String to = "xulab.educacion@gmail.com";
        String from = "xulab.educacion@gmail.com";
        String password = "rxlhyvviztloldvc";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        // Autenticación
        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Crear el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("[SOPORTE XULAB] " + asunto);

            String contenido = "Reporte de: " + correoContacto + "\n\n"
                    + "Mensaje:\n" + descripcion;
            message.setText(contenido);

            // Enviar
            Transport.send(message);

            // Mensaje de éxito en pantalla
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Enviado", "Tu reporte ha sido enviado. Te contactaremos pronto."));

            // Limpiar campos
            this.asunto = "";
            this.correoContacto = "";
            this.descripcion = "";

        } catch (MessagingException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo enviar el correo. Intenta más tarde."));
        }
    }

    // Guía de carrera o ruta de aprendizaje.
    // Clase interna simple para definir un paso (Modelo rápido)
    public static class PasoGuia {

        private int numero;
        private String titulo;
        private String subtitulo;
        private String colorFondo;
        private String colorTexto;

        public PasoGuia(int numero, String titulo, String subtitulo, String colorFondo, String colorTexto) {
            this.numero = numero;
            this.titulo = titulo;
            this.subtitulo = subtitulo;
            this.colorFondo = colorFondo;
            this.colorTexto = colorTexto;
        }

        public int getNumero() {
            return numero;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getSubtitulo() {
            return subtitulo;
        }

        public String getColorFondo() {
            return colorFondo;
        }

        public String getColorTexto() {
            return colorTexto;
        }
    }

    private List<PasoGuia> guiaCarrera;

    @PostConstruct
    public void init() {
        guiaCarrera = new ArrayList<>();

        // 1. Obtenemos los cursos reales de la BD
        List<com.xulab.modelo.Curso> cursosReales = cursoDAO.listarTodos();

        // 2. Los convertimos en "Pasos" para la guía visual
        int contador = 1;
        for (com.xulab.modelo.Curso curso : cursosReales) {

            // Lógica de diseño: El último paso lo destacamos en azul
            boolean esUltimo = (contador == cursosReales.size());
            String colorFondo = esUltimo ? "#134686" : "#e0e0e0";
            String colorTexto = esUltimo ? "white" : "#333";

            // Creamos el paso usando el nombre y descripción del curso real
            guiaCarrera.add(new PasoGuia(
                    contador,
                    curso.getNombre(), // Título dinámico
                    "Módulo Clave", // Subtítulo (podrías usar una parte de la descripción)
                    colorFondo,
                    colorTexto
            ));

            contador++;
        }

        // Si no hay cursos en la BD, agregamos un mensaje por defecto para que no se vea vacío
        if (guiaCarrera.isEmpty()) {
            guiaCarrera.add(new PasoGuia(1, "Próximamente", "Estamos creando contenido", "#e0e0e0", "#333"));
        }
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getCorreoContacto() {
        return correoContacto;
    }

    public void setCorreoContacto(String correoContacto) {
        this.correoContacto = correoContacto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<PasoGuia> getGuiaCarrera() {
        return guiaCarrera;
    }
}
