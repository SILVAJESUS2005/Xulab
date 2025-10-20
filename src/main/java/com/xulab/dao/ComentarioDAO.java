/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xulab.dao;

import com.xulab.modelo.Comentario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author jesus
 */
@Stateless
public class ComentarioDAO {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    /**
     * Busca todos los comentarios asociados a una lección específica.
     *
     * @param leccionId El ID de la lección.
     * @return Una lista de comentarios.
     */
    public List<Comentario> buscarPorLeccionId(int leccionId) {
        TypedQuery<Comentario> query = em.createQuery(
                "SELECT c FROM Comentario c WHERE c.leccion.id = :leccionId ORDER BY c.fechaCreacion DESC", Comentario.class);
        query.setParameter("leccionId", leccionId);
        return query.getResultList();
    }
    
    /**
     * Guarda una nueva entidad de Comentario en la base de datos.
     * @param comentario El objeto Comentario a persistir.
     */
    public void crear(Comentario comentario) {
        em.persist(comentario);
    }
    
}
