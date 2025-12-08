package com.xulab.dao;

import com.xulab.modelo.Leccion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Objeto de acceso a datos (DAO) para la entidad leccion.
 * Maneja las operaciones de persistencia relacionadas con las lecciones individuales.
 * Su función principal es recuperar el contenido, título y detalles de una lección
 * específica para mostrarla en la vista de reproducción.
 * * @author jesus
 */
@Stateless
public class LeccionDAO {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    /**
     * Busca una lección específica por su ID.
     *
     * @param leccionId El ID de la lección a buscar.
     * @return El objeto leccion encontrado o null si no existe.
     */
    public Leccion buscarPorId(int leccionId) {
        return em.find(Leccion.class, leccionId);
    }

}
