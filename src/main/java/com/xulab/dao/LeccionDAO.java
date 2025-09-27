/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xulab.dao;

import com.xulab.modelo.Leccion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author jesus
 */
@Stateless
public class LeccionDAO {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    /**
     * Busca una lección específica por su ID.
     *
     * @param leccionId El ID de la lección a buscar.
     * @return El objeto Leccion encontrado o null si no existe.
     */
    public Leccion buscarPorId(int leccionId) {
        return em.find(Leccion.class, leccionId);
    }

}
