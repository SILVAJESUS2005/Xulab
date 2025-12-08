/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xulab.dao;

import com.xulab.modelo.Curso;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;

/**
 * Objeto de acceso a datos (DAO) para la entidad Curso. Manejar las 
 * operaciones de la base de datos para la entidad Curso.
 * * @author jesus
 */
// DAO significa Data Access Object. La única responsabilidad de esta clase es manejar las operaciones de la base de datos para la entidad Curso
// Stateless indica que esta clase es un Enterprise de JavaBean (EJB).
// Glassfish gestionara su ciclo de vida y nos permitira inyectarla en otras clases.
@Stateless
public class CursoDAO {

    // @PersistenceContext inyecta el EntityManager, que es el objeto principal
    // de JPA para interactuar con la base de datos.
    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    /**
     * Devuelve una lista de todos los cursos de la base de datos.
     *
     * @return Lista de entidades Curso.
     */
    public List<Curso> listarTodos() {
        // Se crea una consulta usando JPQL (Java Persistence Query Lenguage).
        // Es similar a SQL pero se consulta sobre las Entidades Java, no sobre las tablas.
        // "SELECT c FROM Curso c" significa "Selecciona todos los objetos 'c' de la entidad Curso".
        Query query = em.createQuery("SELECT c FROM Curso c");
        return query.getResultList();
    }

    /**
     * Busca una entidad de Curso por su ID (clave primaria).
     *
     * @param id El ID del curso a buscar.
     * @return El objeto Curso si se encuentra, o null si no.
     */
    public Curso buscarPorId(int id) {
        // em.find es el método más simple y directo de JPA 
        // para buscar algo por su clave primaria.
        return em.find(Curso.class, id);
    }

    public long contarCursos() {
        Query query = em.createQuery("SELECT COUNT(c) FROM Curso c");
        return (long) query.getSingleResult();
    }
    
}
