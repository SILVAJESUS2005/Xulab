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
     */
    public List<Integer> obtenerIdsLeccionesCompletadas(Usuario usuario, int cursoId) {
        // Hacemos un JOIN para filtrar solo las lecciones de ESTE curso
        TypedQuery<Integer> query = em.createQuery(
                "SELECT p.leccion.id FROM ProgresoLeccion p "
                + "WHERE p.usuario = :usuario AND p.leccion.modulo.curso.id = :cursoId",
                Integer.class);

        query.setParameter("usuario", usuario);
        query.setParameter("cursoId", cursoId);

        return query.getResultList();
    }
}
