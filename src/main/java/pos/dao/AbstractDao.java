package pos.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

//Abstract class to define the entity manager functions
public abstract class AbstractDao {

    @PersistenceContext
    private EntityManager em;

    protected <T> T getSingle(TypedQuery<T> query) {
        return query.getResultList().stream().findFirst().orElse(null);
    }

    //Take in a query
    protected <T> TypedQuery<T> getQuery(String jpql, Class<T> clazz) {
        return em.createQuery(jpql, clazz);
    }

    protected EntityManager em() {
        return em;
    }
}
