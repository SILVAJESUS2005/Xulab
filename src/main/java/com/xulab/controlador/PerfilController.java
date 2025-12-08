package com.xulab.controlador;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.xulab.dao.InscripcionDAO;
import com.xulab.dao.ProgresoDAO;
import com.xulab.modelo.Curso;
import com.xulab.modelo.Inscripcion;
import com.xulab.modelo.Usuario;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.lowagie.text.Rectangle;

/**
 * Administra los cursos completados y los que se encuentran en progreso
 * para mostrarlos en la sección de peril. Guarda la estructura para generar la}
 * constancia de finalización del curso.
 * @author jesus
 */
@Named("perfilController")
@ViewScoped
public class PerfilController implements Serializable {

    @Inject
    private SessionManager sessionManager;
    @Inject
    private InscripcionDAO inscripcionDAO;
    @Inject
    private ProgresoDAO progresoDAO;

    private Usuario usuario;

    private List<Inscripcion> cursosEnProgreso;
    private List<Inscripcion> cursosCompletados;

    @PostConstruct
    public void init() {
        if (sessionManager.isLoggedIn()) {
            this.usuario = sessionManager.getUsuarioLogueado();

            // 1. Obtenemos todas las inscripciones
            List<Inscripcion> todas = inscripcionDAO.buscarPorUsuario(this.usuario);

            // 2. Inicializamos las listas
            cursosEnProgreso = new ArrayList<>();
            cursosCompletados = new ArrayList<>();

            // 3. Clasificamos cada curso
            for (Inscripcion i : todas) {
                boolean completo = progresoDAO.isCursoCompletado(usuario.getId(), i.getCurso().getId());
                if (completo) {
                    cursosCompletados.add(i);
                } else {
                    cursosEnProgreso.add(i);
                }
            }
        }
    }

    // --- LÓGICA DE GENERACIÓN DE PDF ---
    public void descargarConstancia(Curso curso) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment; filename=Constancia_" + curso.getNombre().replaceAll(" ", "_") + ".pdf");

        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // COLORES Y FUENTES
            Color colorPrimario = new Color(19, 70, 134);
            Color colorAcento = new Color(0, 168, 132);
            Color colorTextoGris = new Color(80, 80, 80);

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 34, colorPrimario);
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 14, colorTextoGris);
            Font fontNombre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32, colorAcento);
            Font fontCurso = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, colorPrimario);
            Font fontFirma = FontFactory.getFont(FontFactory.HELVETICA, 12, colorTextoGris);

            // MARCO
            PdfPTable mainTable = new PdfPTable(1);
            mainTable.setWidthPercentage(100);
            mainTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            PdfPCell frameCell = new PdfPCell();
            frameCell.setBorder(Rectangle.BOX);
            frameCell.setBorderColor(colorAcento);
            frameCell.setBorderWidth(5f);
            frameCell.setPadding(20f);
            frameCell.setPaddingBottom(40f);

            // ENCABEZADO
            Paragraph tituloConstancia = new Paragraph("CONSTANCIA DE FINALIZACIÓN", fontTitulo);
            tituloConstancia.setAlignment(Element.ALIGN_CENTER);
            tituloConstancia.setSpacingAfter(10);
            frameCell.addElement(tituloConstancia);

            try {
                String logoPath = facesContext.getExternalContext().getRealPath("/resources/images/Logo4.png");
                Image logo = Image.getInstance(logoPath);
                logo.scaleToFit(120, 60);
                logo.setAlignment(Element.ALIGN_CENTER);
                logo.setSpacingAfter(20);
                frameCell.addElement(logo);
            } catch (Exception e) {
            }

            // CUERPO
            Paragraph textoOtorga = new Paragraph("Xulab Education Platform otorga la presente constancia a:", fontSubtitulo);
            textoOtorga.setAlignment(Element.ALIGN_CENTER);
            textoOtorga.setSpacingBefore(10);
            textoOtorga.setSpacingAfter(15);
            frameCell.addElement(textoOtorga);

            Paragraph pNombre = new Paragraph(usuario.getNombre().toUpperCase(), fontNombre);
            pNombre.setAlignment(Element.ALIGN_CENTER);
            pNombre.setSpacingAfter(5);
            frameCell.addElement(pNombre);

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

            Paragraph pCurso = new Paragraph(curso.getNombre(), fontCurso);
            pCurso.setAlignment(Element.ALIGN_CENTER);
            pCurso.setSpacingBefore(10);
            pCurso.setSpacingAfter(40);
            frameCell.addElement(pCurso);

            // PIE
            PdfPTable footerTable = new PdfPTable(new float[]{1, 1, 1});
            footerTable.setWidthPercentage(100);
            footerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            footerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);

            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
            String fechaStr = sdf.format(new Date());
            footerTable.addCell(new Paragraph("Fecha de emisión:\n" + fechaStr, fontFirma));
            footerTable.addCell("");
            footerTable.addCell(new Paragraph("_______________________\nXulab Education", fontFirma));

            frameCell.addElement(footerTable);
            mainTable.addCell(frameCell);
            document.add(mainTable);

            document.close();
            facesContext.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<Inscripcion> getCursosEnProgreso() {
        return cursosEnProgreso;
    }

    public List<Inscripcion> getCursosCompletados() {
        return cursosCompletados;
    }
}
