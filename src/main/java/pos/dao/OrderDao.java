package pos.dao;

import org.springframework.stereotype.Repository;
import pos.pojo.OrderPojo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

//Repository for order
@Repository
public class OrderDao extends AbstractDao{


    @PersistenceContext
    private EntityManager em;

    //Add order
    @Transactional
    public int insert(OrderPojo orderPojo){
        em.persist(orderPojo);
        em.flush();
        return orderPojo.getId();
    }

    //Retrieve an Order by id
    public OrderPojo select(int id) {
        return em.find(OrderPojo.class, id);
    }

    //Retrieve all Orders
    public List<OrderPojo> selectAll() {
        String select_all = "select p from OrderPojo p";
        TypedQuery<OrderPojo> query = getQuery(select_all,OrderPojo.class);
        return query.getResultList();
    }

    //todo remove delete
    //Delete an order by id
    public void delete(int id) {
        OrderPojo orderPojo = em.find(OrderPojo.class, id);
        em.remove(orderPojo);
    }
}
