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
    private EntityManager em;

    //Insert into table
    @Transactional
    public void insert(BrandPojo brandPojo){
        em.persist(brandPojo);
    }

    //Retrieve a brand pojo
    @Transactional
    public BrandPojo select(int id){
        return em.find(BrandPojo.class,id);
    }

    //Retrieve all brand pojo
    @Transactional
    public List<BrandPojo> selectAll() {
        //Queries needed for brand
        String select_all = "select p from BrandPojo p";
        TypedQuery<BrandPojo> query = getQuery(select_all,  BrandPojo.class);
        if(query == null){
            return new ArrayList<>();
        }
        return query.getResultList();
    }

    //Update a brand with given brandId
    @Transactional
    public void update(int id,BrandPojo brandPojo) {
        BrandPojo brandPojo1=em.find(BrandPojo.class, id);
        brandPojo1.setBrand(brandPojo.getBrand());
        brandPojo1.setCategory(brandPojo.getCategory());
        em.merge(brandPojo1);
    }

    @Transactional
    //Retrieve brand pojo based in brand and category
    public List<BrandPojo> getIdFromBrandCategory(String brand, String category){
        String select_brand_category_id = "select p from BrandPojo p where brand=:brand and category=:category";
        TypedQuery<BrandPojo> query = getQuery(select_brand_category_id, BrandPojo.class);
        query.setParameter("brand",brand);
        query.setParameter("category",category);
        List<BrandPojo> brandPojoList= query.getResultList();
        return brandPojoList;
    }
}
