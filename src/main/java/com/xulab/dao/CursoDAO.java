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
 *
 * @author jesus
 */
// DAO significa Data Access Object. La única responsabilidad de esta clase es manejar las operaciones de la base de datos para la entidad Curso

// Stateless indica que esta clase es un Enterprise de JavaBean (EJB).
// Glassfish gestionara su ciclo de vida y nos permitira inyectarla en otras clases.

@Stateless
public class CursoDAO {
    
    // @PersistenceContext inyecta el EntityManager, que es el objeto principal
    // de JPA para interactuar con la base de datos.
    @PersistenceContext(unitName = "my_persistence_unit") // Asegurate que el unitName coincida con el de tu persistence.xml
    private EntityManager em;
    
        /**
     * Devuelve una lista de todos los cursos de la base de datos.
     * @return Lista de entidades Curso.
     */
    
    public List<Curso> listarTodos() {
        // Se crea una consulta usando JPQL (Java Persistence Query Lenguage).
        // Es similar a SQL pero se consulta sobre las Entidades Java, no sobre las tablas.
        // "SELECT c FROM Curso c" significa "Selecciona todos los objetos 'c' de la entidad Curso".
        Query query = em.createQuery("SELECT c FROM Curso c");
        return query.getResultList();
    }
    
    // Aquí en el futuro podrías agregar otros métodos como:
    // public void crear(Curso curso) { ... }
    // public Curso buscarPorId(int id) { ... }
    // public void actualizar(Curso curso) { ... }
}
