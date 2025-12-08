package com.xulab.dao;

import com.xulab.modelo.Leccion;
import com.xulab.modelo.ProgresoLeccion;
import com.xulab.modelo.Usuario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Objeto de acceso a datos (DAO) para la entidad ProgresoLeccion. Esta clase
 * gestiona toda la lógica de seguimiento del aprendizaje del usuario. Permite
 * registrar lecciones completadas, verificar el estado de avance y determinar
 * si un curso ha sido finalizado en su totalidad.
 *
 * * @author jesus
 */
@Stateless
public class ProgresoDAO {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public void guardar(ProgresoLeccion progreso) {
        em.persist(progreso);
    }

    // Verifica si una lección específica ya fue completada por el usuario
    public boolean esLeccionCompletada(Usuario usuario, Leccion leccion) {
        TypedQuery<ProgresoLeccion> query = em.createQuery(
                "SELECT p FROM ProgresoLeccion p WHERE p.usuario = :usuario AND p.leccion = :leccion",
                ProgresoLeccion.class);
        query.setParameter("usuario", usuario);
        query.setParameter("leccion", leccion);

        return !query.getResultList().isEmpty();
    }

    // Cuenta cuántas lecciones ha completado un usuario DENTRO de un curso específico
    // Nota: Esto usa un JOIN implícito a través de Leccion -> Modulo -> Curso
    public long contarCompletadasPorCurso(Usuario usuario, int cursoId) {
        Query query = em.createQuery(
                "SELECT COUNT(p) FROM ProgresoLeccion p WHERE p.usuario = :usuario AND p.leccion.modulo.curso.id = :cursoId");
        query.setParameter("usuario", usuario);
        query.setParameter("cursoId", cursoId);

        return (long) query.getSingleResult();
    }

    /**
     * Obtiene una lista de IDs de las lecciones que el usuario ya completó en
     * un curso. Esto es mucho más eficiente que preguntar lección por lección.
     *
     * @param usuario
     * @param cursoId
     * @return Regresa las ID´s de las lecciones completadas de un modulo y
     * curso por usuario.
     */
    public List<Integer> obtenerIdsLeccionesCompletadas(Usuario usuario, int cursoId) {
        // Hacemos un JOIN para filtrar solo las lecciones de este curso.
        TypedQuery<Integer> query = em.createQuery(
                "SELECT p.leccion.id FROM ProgresoLeccion p "
                + "WHERE p.usuario = :usuario AND p.leccion.modulo.curso.id = :cursoId",
                Integer.class);

        query.setParameter("usuario", usuario);
        query.setParameter("cursoId", cursoId);

        return query.getResultList();
    }

    /**
     * Verifica si un usuario ha completado todas las lecciones de un curso
     *
     * @param usuarioId.
     * @param cursoId
     * @return Regresa si el usuario ha completado todas las lecciones del
     * curso, false si falta alguna.
     */
    public boolean isCursoCompletado(int usuarioId, int cursoId) {
        // 1. Contar total de lecciones del curso
        Query qTotal = em.createQuery("SELECT COUNT(l) FROM Leccion l WHERE l.modulo.curso.id = :cursoId");
        qTotal.setParameter("cursoId", cursoId);
        long totalLecciones = (long) qTotal.getSingleResult();

        if (totalLecciones == 0) {
            return false; // Curso vacío no se considera completado
        }
        // 2. Contar lecciones completadas por el usuario en ese curso
        Query qCompletadas = em.createQuery("SELECT COUNT(p) FROM ProgresoLeccion p WHERE p.usuario.id = :usuarioId AND p.leccion.modulo.curso.id = :cursoId");
        qCompletadas.setParameter("usuarioId", usuarioId);
        qCompletadas.setParameter("cursoId", cursoId);
        long leccionesCompletadas = (long) qCompletadas.getSingleResult();

        // 3. Comparar
        return totalLecciones == leccionesCompletadas;
    }
}
