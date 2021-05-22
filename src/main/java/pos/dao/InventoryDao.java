package pos.dao;

import org.springframework.stereotype.Repository;
import pos.pojo.InventoryPojo;
import pos.pojo.ProductPojo;

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
        inventoryPojo1.setProductId(inventoryPojo.getProductId());
        inventoryPojo1.setQuantity(inventoryPojo.getQuantity());
        em.merge(inventoryPojo1);
    }

    //get from product Id
    public InventoryPojo getFromProductId(int productId){
        String select="select p from InventoryPojo p where productId=:productId";
        TypedQuery<InventoryPojo> query = getQuery(select, InventoryPojo.class);
        query.setParameter("productId", productId);
        List<InventoryPojo> res=query.getResultList();
        if(res.size()>0){
            return res.get(0);
        }
        else
            return null;
    }
}
