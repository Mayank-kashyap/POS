package pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.BrandDao;
import pos.model.BrandData;
import pos.model.BrandForm;
import pos.pojo.BrandPojo;
import pos.util.StringUtil;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
public class BrandService {

    @Autowired
    private BrandDao dao;

    //Add a brand
    @Transactional(rollbackOn = ApiException.class)
    public void add(BrandPojo brandPojo) throws ApiException{
        checkNull(brandPojo);
        normalize(brandPojo);
        dao.insert(brandPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional
    public List<BrandPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, BrandPojo p) throws ApiException {
        checkNull(p);
        normalize(p);
        BrandPojo ex = getCheck(id);
        ex.setBrand(p.getBrand());
        ex.setCategory(p.getCategory());
        dao.update(id,ex);
    }


    @Transactional
    public BrandPojo getCheck(int id) throws ApiException {
        BrandPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Brand with given ID does not exit, id: " + id);
        }
        return p;
    }

    public void checkNull(BrandPojo brandPojo) throws ApiException {
        if(StringUtil.isEmpty(brandPojo.getBrand())) {
            throw new ApiException("Brand name cannot be empty");
        }
        if(StringUtil.isEmpty(brandPojo.getCategory())) {
            throw new ApiException("Category name cannot be empty");
        }
        List<BrandPojo> brand_category_list= dao.getIdFromBrandCategory(brandPojo.getBrand(),brandPojo.getCategory());
        if(!brand_category_list.isEmpty()) {
            throw new ApiException("Brand and Category already exist");
        }
    }

    @Transactional()
    public BrandPojo getBrandPojo(String brand, String category) throws ApiException {

        List<BrandPojo> brand_list = dao.getIdFromBrandCategory(brand, category);

        if (brand_list.isEmpty()) {
            throw new ApiException("The brand name and category given does not exist " + brand + " " + category);
        }
        return brand_list.get(0);
    }

    protected static void normalize(BrandPojo p) {
        p.setBrand(StringUtil.toLowerCase(p.getBrand()));
        p.setCategory(StringUtil.toLowerCase(p.getCategory()));
    }

    //HELPER FUNCTIONS
    //Converts a brand pojo into brand data
    public static BrandData convert(BrandPojo p) {
        BrandData d = new BrandData();
        d.setBrand(p.getBrand());
        d.setCategory(p.getCategory());
        d.setId(p.getId());
        return d;
    }

    //converts list of brand pojo to list of brand data
    public static List<BrandData> convert(List<BrandPojo> list) {
        List<BrandData> list2 = new ArrayList<BrandData>();
        for (BrandPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    //converts a brand form into brand pojo
    @Transactional
    public static BrandPojo convert(BrandForm f) {
        BrandPojo p = new BrandPojo();
        p.setBrand(f.getBrand());
        p.setCategory(f.getCategory());
        return p;
    }
}
