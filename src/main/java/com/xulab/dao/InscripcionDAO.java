package com.xulab.dao;

import com.xulab.modelo.Curso;
import com.xulab.modelo.Inscripcion;
import com.xulab.modelo.Usuario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Objeto de acceso a datos (DAO) para la entidad Inscripcion. Gestiona la
 * persistencia de las relaciones entre Usuarios y Cursos. Sus funciones
 * principales son registrar nuevas inscripciones y consultar el estado de
 * matrícula de los estudiantes.
 *
 * * @author jesus
 */
@Stateless
public class InscripcionDAO {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    /**
     * Guarda una nueva inscripción en la base de datos.
     *
     * @param inscripcion El objeto Inscripcion a persistir.
     */
    public void crear(Inscripcion inscripcion) {
        em.persist(inscripcion);
    }

    /**
     * Busca si ya existe una inscripción para un usuario y curso específicos.
     *
     * @param usuario El usuario a verificar.
     * @param curso El curso a verificar.
     * @return La entidad Inscripcion si existe, o null si no.
     */
    public Inscripcion buscarPorUsuarioYCurso(Usuario usuario, Curso curso) {
        TypedQuery<Inscripcion> query = em.createQuery(
                "SELECT i FROM Inscripcion i WHERE i.usuario = :usuario AND i.curso = :curso",
                Inscripcion.class);

        query.setParameter("usuario", usuario);
        query.setParameter("curso", curso);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            // Es normal que no haya resultados, significa que el usuario no está inscrito.
            return null;
        }
    }

    /**
     * Busca todas las inscripciones de un usuario específico.
     *
     * @param usuario El usuario del que se quieren buscar las inscripciones.
     * @return Una lista de entidades Inscripcion.
     */
    public List<Inscripcion> buscarPorUsuario(Usuario usuario) {
        TypedQuery<Inscripcion> query = em.createQuery(
                "SELECT i FROM Inscripcion i WHERE i.usuario = :usuario",
                Inscripcion.class);
        query.setParameter("usuario", usuario);
        return query.getResultList();
    }

    /**
     * Cuenta el número total de inscripciones activas en la plataforma.
     * Utilizado para mostrar estadísticas en la página de inicio (Landing
     * Page).
     * @return El número total de registros en la tabla de inscripciones.
     */
    public long contarInscripciones() {
        Query query = em.createQuery("SELECT COUNT(i) FROM Inscripcion i");
        return (long) query.getSingleResult();
    }
}
