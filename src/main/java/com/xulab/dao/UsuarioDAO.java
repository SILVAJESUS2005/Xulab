package com.xulab.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.xulab.modelo.Usuario;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

/**
 * Objeto de acceso a datos (DAO) para la entidad usuario.
 * Esta clase encapsula toda la lógica de interacción con la base de datos relacionada
 * con los usuarios. Se encarga de las operaciones CRUD (crear, leer, actualizar)
 * y de consultas específicas como buscar por correo o token.
 * * @author jesus
 */
@Stateless
public class UsuarioDAO {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param usuario El objeto usuario a persistir.
     */
    public void crear(Usuario usuario) {
        em.persist(usuario);
    }

    /**
     * Busca un usuario por su dirección de email.
     *
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

    /**
     * Busca un usuario por su token de verificación.
     * @param token
     * @return El objeto usuario si se encuentra el token correspondiente,o null si no existe.
     */
    public Usuario buscarPorToken(String token) {
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.tokenVerificacion = :token", Usuario.class);
            query.setParameter("token", token);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Actualiza un usuario existente (útil para cambiar 'verificado' a true).
     * @param usuario
     */
    public void actualizar(Usuario usuario) {
        em.merge(usuario);
    }

}
