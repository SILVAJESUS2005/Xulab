package com.xulab.controlador;

import com.xulab.dao.CursoDAO;
import com.xulab.dao.InscripcionDAO;
import com.xulab.dao.ModuloDAO;
import com.xulab.dao.ProgresoDAO;
import com.xulab.modelo.Curso;
import com.xulab.modelo.Inscripcion;
import com.xulab.modelo.Modulo;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private CursoDAO cursoDAO;

    @Inject
    private SessionManager sessionManager;

    @Inject
    private ProgresoDAO progresoDAO;

    private Curso cursoActual;
    private List<Modulo> modulos;
    private boolean usuarioInscrito = false;
    private Set<Integer> leccionesCompletadasIds = new HashSet<>();
    private Map<Integer, Integer> mapaNumeracionVisual;

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
                calcularNumeracionVisual();
                cargarProgreso(idCurso);
                // Verificamos el estado de la inscripción al cargar la página
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

    // 1. Verifica si una lección individual está completa
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

    // Método para verificar si el usuario está inscrito
    private void verificarInscripcion() {
        if (sessionManager.isLoggedIn() && cursoActual != null) {
            Inscripcion inscripcion = inscripcionDAO.buscarPorUsuarioYCurso(sessionManager.getUsuarioLogueado(), cursoActual);
            this.usuarioInscrito = (inscripcion != null);
        }
    }

    // Método para realizar la inscripción
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
     *
     * @return numero que representa el porcentaje de avance del curso tomado.
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

    /**
     * Recorre los módulos y lecciones para asignarles un número consecutivo (1,
     * 2, 3...) independiente de su ID en la base de datos.
     */
    private void calcularNumeracionVisual() {
        mapaNumeracionVisual = new HashMap<>();
        int contador = 1;

        if (modulos != null) {
            for (Modulo m : modulos) {
                if (m.getLecciones() != null) {
                    for (com.xulab.modelo.Leccion l : m.getLecciones()) {
                        mapaNumeracionVisual.put(l.getId(), contador++);
                    }
                }
            }
        }
    }

    public void descargarCertificado() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment; filename=Constancia_Xulab.pdf");

        // Configurar documento horizontal
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // --- COLORES ---
            Color colorPrimario = new Color(19, 70, 134);   // #134686 (Azul)
            Color colorAcento = new Color(0, 168, 132);     // #00A884 (Verde Teal)
            Color colorTextoGris = new Color(80, 80, 80);

            // --- FUENTES ---
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 34, colorPrimario);
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 14, colorTextoGris);
            Font fontNombre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32, colorAcento);
            Font fontCurso = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, colorPrimario);
            Font fontFirma = FontFactory.getFont(FontFactory.HELVETICA, 12, colorTextoGris);

            // --- MARCO (Borde) ---
            PdfPTable mainTable = new PdfPTable(1);
            mainTable.setWidthPercentage(100);
            mainTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            PdfPCell frameCell = new PdfPCell();
            frameCell.setBorder(Rectangle.BOX);
            frameCell.setBorderColor(colorAcento);
            frameCell.setBorderWidth(5f);
            frameCell.setPadding(20f);
            frameCell.setPaddingBottom(40f); // Un poco más de espacio abajo

            // ==========================================
            // 1. ENCABEZADO
            // ==========================================
            // A) Título
            Paragraph tituloConstancia = new Paragraph("CONSTANCIA DE FINALIZACIÓN", fontTitulo);
            tituloConstancia.setAlignment(Element.ALIGN_CENTER);
            tituloConstancia.setSpacingAfter(10);
            frameCell.addElement(tituloConstancia);

            // B) Logo Centrado (Debajo del título)
            try {
                String logoPath = facesContext.getExternalContext().getRealPath("/resources/images/Logo3.png");

                Image logo = Image.getInstance(logoPath);
                logo.scaleToFit(120, 60); // Ajusta el tamaño (ancho, alto)
                logo.setAlignment(Element.ALIGN_CENTER);
                logo.setSpacingAfter(20);
                frameCell.addElement(logo);
            } catch (Exception e) {
                System.out.println("Error cargando logo para PDF: " + e.getMessage());
            }

            // ==========================================
            // 2. CUERPO
            // ==========================================
            Paragraph textoOtorga = new Paragraph("Xulab Education Platform otorga la presente constancia a:", fontSubtitulo);
            textoOtorga.setAlignment(Element.ALIGN_CENTER);
            textoOtorga.setSpacingBefore(10);
            textoOtorga.setSpacingAfter(15);
            frameCell.addElement(textoOtorga);

            // Nombre del Alumno
            Paragraph pNombre = new Paragraph(sessionManager.getUsuarioLogueado().getNombre().toUpperCase(), fontNombre);
            pNombre.setAlignment(Element.ALIGN_CENTER);
            pNombre.setSpacingAfter(5);
            frameCell.addElement(pNombre);

            // Línea decorativa pequeña debajo del nombre
            PdfPTable lineTable = new PdfPTable(1);
            lineTable.setWidthPercentage(40);
            PdfPCell lineCell = new PdfPCell(new Phrase(" "));
            lineCell.setBorder(Rectangle.BOTTOM);
            lineCell.setBorderColor(colorAcento);
            lineCell.setBorderWidth(1.5f);
            lineTable.addCell(lineCell);
            frameCell.addElement(lineTable);

            Paragraph textoCurso = new Paragraph("\nPor haber completado satisfactoriamente el curso en línea:", fontSubtitulo);
            textoCurso.setAlignment(Element.ALIGN_CENTER);
            textoCurso.setSpacingBefore(15);
            frameCell.addElement(textoCurso);

            // Nombre del Curso
            Paragraph pCurso = new Paragraph(cursoActual.getNombre(), fontCurso);
            pCurso.setAlignment(Element.ALIGN_CENTER);
            pCurso.setSpacingBefore(10);
            pCurso.setSpacingAfter(40);
            frameCell.addElement(pCurso);

            // ==========================================
            // 3. PIE DE PÁGINA (Fecha y Firma)
            // ==========================================
            PdfPTable footerTable = new PdfPTable(new float[]{1, 1, 1});
            footerTable.setWidthPercentage(100);
            footerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            footerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);

            // Fecha (Izquierda)
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
            String fechaStr = sdf.format(new Date());
            Paragraph pFecha = new Paragraph("Fecha de emisión:\n" + fechaStr, fontFirma);
            pFecha.setAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(pFecha);

            // Centro vacío
            footerTable.addCell("");

            // Firma (Derecha) - MODIFICADO
            Paragraph pFirma = new Paragraph("FINALIZADO\n_______________________\nXulab Education", fontFirma);
            pFirma.setAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(pFirma);

            frameCell.addElement(footerTable);

            // --- FIN DEL CONTENIDO ---
            mainTable.addCell(frameCell);
            document.add(mainTable);

            document.close();
            facesContext.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int obtenerNumeroVisual(int leccionId) {
        return mapaNumeracionVisual.getOrDefault(leccionId, 0);
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
