package pos.dao;

import org.springframework.stereotype.Repository;
import pos.pojo.InventoryPojo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

//Repository for inventory
@Repository
public class InventoryDao extends AbstractDao{

    @PersistenceContext
    private EntityManager em;

    //Insert into table
    @Transactional
    public void insert(InventoryPojo inventoryPojo){
        em.persist(inventoryPojo);
    }

    //Retrieve an inventory pojo with id
    public InventoryPojo select(int id){
        return em.find(InventoryPojo.class,id);
    }

    //Retrieve list of inventory pojo
    public List<InventoryPojo> selectAll() {
        //Query required for inventory
        String select_all = "select p from InventoryPojo p";
        TypedQuery<InventoryPojo> query = getQuery(select_all,  InventoryPojo.class);
        if(query == null){
            return new ArrayList<>();
        }
        return query.getResultList();
    }

    //Update an inventory
    public void update(int id,InventoryPojo inventoryPojo) {
        InventoryPojo inventoryPojo1=em.find(InventoryPojo.class, id);
        inventoryPojo1.setProduct(inventoryPojo.getProduct());
        inventoryPojo1.setQuantity(inventoryPojo.getQuantity());
        em.merge(inventoryPojo1);
    }
}
