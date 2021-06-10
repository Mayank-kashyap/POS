package pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.BrandDao;
import pos.pojo.BrandPojo;
import pos.util.StringUtil;

import javax.transaction.Transactional;
import java.util.List;


@Service
public class BrandService {

    @Autowired
    private BrandDao brandDao;

    //Add a brand
    @Transactional(rollbackOn = ApiException.class)
    public void add(BrandPojo brandPojo) throws ApiException{

        normalize(brandPojo);
        check(brandPojo);
        brandDao.insert(brandPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void addList(List<BrandPojo> brandPojoList) throws ApiException {
        for (BrandPojo brandPojo: brandPojoList){
            normalize(brandPojo);
            check(brandPojo);
        }
        for (BrandPojo brandPojo: brandPojoList){
            add(brandPojo);
        }
    }
    //get a brand by id
    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    //get list of all brands
    @Transactional
    public List<BrandPojo> getAll() {
        return brandDao.selectAll();
    }

    //update a brand
    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, BrandPojo brandPojo) throws ApiException {
        normalize(brandPojo);
        check(brandPojo);
        BrandPojo brandPojo1 = getCheck(id);
        brandPojo1.setBrand(brandPojo.getBrand());
        brandPojo1.setCategory(brandPojo.getCategory());
        brandDao.update(id,brandPojo1);
    }


    //HELPER METHODS
    //check whether brand with given id exists or not
    @Transactional
    public BrandPojo getCheck(int id) throws ApiException {
        BrandPojo brandPojo = brandDao.select(id);
        if (brandPojo == null) {
            throw new ApiException("Brand with given ID does not exit, id: " + id);
        }
        return brandPojo;
    }

    //checks whether the entered values are null or not
    public void check(BrandPojo brandPojo) throws ApiException {
        if(StringUtil.isEmpty(brandPojo.getBrand())) {
            throw new ApiException("Brand name cannot be empty");
        }
        if(StringUtil.isEmpty(brandPojo.getCategory())) {
            throw new ApiException("Category name cannot be empty");
        }
        List<BrandPojo> brandPojoList = brandDao.getIdFromBrandCategory(brandPojo.getBrand(),brandPojo.getCategory());
        if(!brandPojoList.isEmpty()) {
            throw new ApiException("Brand and Category already exist: "+brandPojo.getBrand()+" "+brandPojo.getCategory());
        }
    }

    //gets a brand by brand and category
    @Transactional()
    public BrandPojo getBrandPojo(String brand, String category) throws ApiException {

        List<BrandPojo> brandPojoList = brandDao.getIdFromBrandCategory(brand, category);
        if (brandPojoList.isEmpty()) {
            throw new ApiException("The brand name and category given does not exist " + brand + " " + category);
        }
        return brandPojoList.get(0);
    }

    //normalizes a pojo into lower case and trimmed
    protected static void normalize(BrandPojo brandPojo) {
        brandPojo.setBrand(StringUtil.toLowerCase(brandPojo.getBrand()));
        brandPojo.setCategory(StringUtil.toLowerCase(brandPojo.getCategory()));
    }

}
