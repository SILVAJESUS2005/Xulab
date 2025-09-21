/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xulab.dao;

import com.xulab.modelo.Modulo;
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
public class ModuloDAO {
     @PersistenceContext(unitName = "my_persistence_unit") // Nombre la unit name de la persistencia que configuraste en tu archivo persistence 
    private EntityManager em;
    
    public List<Modulo> buscarPorCursoId(int cursoId) {
        // Usamos una consulta JPQL para traer los módulos de un curso específico
        TypedQuery<Modulo> query = em.createQuery(
                "SELECT m FROM Modulo m WHERE m.curso.id = :cursoId", Modulo.class);
        query.setParameter("cursoId", cursoId);
        return query.getResultList();
    }
}
