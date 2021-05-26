package pos.dao;

import org.springframework.stereotype.Repository;
import pos.pojo.BrandPojo;
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

    @Transactional
    public void update(int id,OrderPojo orderPojo){
        OrderPojo orderPojo1=em.find(OrderPojo.class,id);
        orderPojo1.setIsInvoiceGenerated(orderPojo.getIsInvoiceGenerated());
        orderPojo1.setDatetime(orderPojo.getDatetime());
        em.merge(orderPojo1);
    }

    //Retrieve an Order by id
    @Transactional
    public OrderPojo select(int id) {
        return em.find(OrderPojo.class, id);
    }

    //Retrieve all Orders
    @Transactional
    public List<OrderPojo> selectAll() {
        String select_all = "select p from OrderPojo p";
        TypedQuery<OrderPojo> query = getQuery(select_all,OrderPojo.class);
        return query.getResultList();
    }
}
