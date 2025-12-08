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
 * Objeto de acceso a datos (DAO) para la entidad modulo. Se encarga de las
 * operaciones de base de datos relacionadas con los módulos de los cursos. Su
 * función principal es recuperar la estructura jerárquica de los cursos
 * (Módulos -> Lecciones). Es un EJB de tipo.
 *
 * * @author jesus
 */
@Stateless
public class ModuloDAO {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    /**
     * Obtiene la lista de módulos asociados a un curso específico.
     * Este método es fundamental para construir el temario del curso en la
     * vista. 
     * @param cursoId El ID del curso del cual se quieren obtener los
     * módulos.
     * @return Una lista de objetos modulo, que a su vez contendrán sus
     * lecciones (FetchType.EAGER).
     */
    public List<Modulo> buscarPorCursoId(int cursoId) {
        // Usamos una consulta JPQL para traer los módulos de un curso específico
        TypedQuery<Modulo> query = em.createQuery(
                "SELECT m FROM Modulo m WHERE m.curso.id = :cursoId", Modulo.class);
        query.setParameter("cursoId", cursoId);
        return query.getResultList();
    }
}
