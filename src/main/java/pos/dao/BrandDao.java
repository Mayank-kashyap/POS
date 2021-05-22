package pos.dao;

import org.springframework.stereotype.Repository;
import pos.pojo.BrandPojo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

//Repository for brand
@Repository
public class BrandDao extends AbstractDao{

    @PersistenceContext
    private EntityManager entityManager;

    //Insert into table
    @Transactional
    public void insert(BrandPojo brandPojo){
        entityManager.persist(brandPojo);
    }

    //Retrieve a brand pojo
    @Transactional
    public BrandPojo select(int id){
        return entityManager.find(BrandPojo.class,id);
    }

    //Retrieve all brand pojo
    @Transactional
    public List<BrandPojo> selectAll() {
        String selectAll = "select p from BrandPojo p";
        TypedQuery<BrandPojo> query = getQuery(selectAll,  BrandPojo.class);
        if(query == null){
            return new ArrayList<>();
        }
        return query.getResultList();
    }

    //Update a brand with given brandId
    @Transactional
    public void update(int id,BrandPojo brandPojo) {
        BrandPojo brandPojo1= entityManager.find(BrandPojo.class, id);
        brandPojo1.setBrand(brandPojo.getBrand());
        brandPojo1.setCategory(brandPojo.getCategory());
        entityManager.merge(brandPojo1);
    }

    @Transactional
    //Retrieve brand pojo based in brand and category
    public List<BrandPojo> getIdFromBrandCategory(String brand, String category){
        String selectBrandCategory = "select p from BrandPojo p where brand=:brand and category=:category";
        TypedQuery<BrandPojo> query = getQuery(selectBrandCategory, BrandPojo.class);
        query.setParameter("brand",brand);
        query.setParameter("category",category);
        List<BrandPojo> brandPojoList= query.getResultList();
        return brandPojoList;
    }
}
