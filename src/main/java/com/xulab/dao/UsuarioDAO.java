/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xulab.dao;

import com.xulab.modelo.Usuario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.xulab.modelo.Usuario;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
/**
 *
 * @author jesus
 */
@Stateless
public class UsuarioDAO {
    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;
    /**
     * Guarda un nuevo usuario en la base de datos.
     * @param usuario El objeto Usuario a persistir.
     */
    public void crear(Usuario usuario) {
        em.persist(usuario);
    }
    
    /**
     * Busca un usuario por su dirección de email.
     * @param email El email del usuario a buscar.
     * @return El objeto Usuario si se encuentra, o null si no existe.
     */
    public Usuario buscarPorEmail(String email) {
        // Creamos una consulta de JPQL para buscar un Usuario por su campo ´email´.
        TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class);
        
        //Asignamos el valor del parámetro ´email´ a la consulta.
        query.setParameter("email", email);
        
        try {
            // Intentamos ejecutar la consulta y obtener un único resultado.
            return query.getSingleResult();
        } catch (NoResultException e) {
           // Si la consulta no devuelve ningún resultado, JPA lanza esta excepción.
           // La capturamos y devolvemos null para indicar que no se encontró el usuario.  
           return null;
        }
    }
    
}
