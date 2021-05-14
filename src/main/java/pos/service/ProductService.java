package pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.BrandDao;
import pos.dao.InventoryDao;
import pos.dao.ProductDao;
import pos.model.ProductData;
import pos.model.ProductForm;
import pos.pojo.BrandPojo;
import pos.pojo.ProductPojo;
import pos.util.StringUtil;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductDao dao;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private BrandService brandService;

    @Transactional(rollbackOn = ApiException.class)
    public void add(ProductPojo productPojo) throws ApiException{
        check(productPojo);
        normalize(productPojo);
        dao.insert(productPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional
    public List<ProductPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, ProductPojo p) throws ApiException {
        check(p);
        normalize(p);
        ProductPojo ex = getCheck(id);
        ex.setBarcode(p.getBarcode());
        ex.setName(p.getName());
        ex.setMrp(p.getMrp());
        dao.update(id,ex);
    }

    @Transactional
    public void check(ProductPojo productPojo) throws ApiException {
        if(StringUtil.isEmpty(productPojo.getBarcode())) {
            throw new ApiException("Barcode cannot be empty");
        }
        if(StringUtil.isEmpty(productPojo.getName())) {
            throw new ApiException("Name cannot be empty");
        }
        if(productPojo.getMrp()<0)
            throw new ApiException("Mrp cannot be negative");

    }
    @Transactional
    public ProductPojo getCheck(int id) throws ApiException {
        ProductPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Product with given ID does not exist, id: " + id);
        }
        return p;
    }

    //maps all the product pojos with their barcode
    @Transactional
    public Map<String, ProductPojo> getAllProductPojosByBarcode() {
        List<ProductPojo> product_list = getAll();
        Map<String, ProductPojo> barcode_product = new HashMap<String, ProductPojo>();
        for (ProductPojo product : product_list) {
            barcode_product.put(product.getBarcode(), product);
        }
        return barcode_product;
    }

    @Transactional
    protected static void normalize(ProductPojo p) {
        p.setName(StringUtil.toLowerCase(p.getName()));
        p.setBarcode(StringUtil.toLowerCase(p.getBarcode()));
    }

    @Transactional
    public ProductData convert(ProductPojo p) throws ApiException {
        ProductData d = new ProductData();
        d.setBarcode(p.getBarcode());
        d.setBrand((brandDao.select(p.getBrandPojo().getId())).getBrand());
        d.setCategory((brandDao.select(p.getBrandPojo().getId())).getCategory());
        d.setName(p.getName());
        d.setMrp(p.getMrp());
        d.setId(p.getId());
        return d;
    }

    @Transactional
    public ProductPojo convert(ProductForm f) throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode(f.getBarcode());
        p.setName(f.getName());
        p.setMrp(f.getMrp());
        f.setBrand(f.getBrand().toLowerCase().trim());
        f.setCategory(f.getCategory().toLowerCase().trim());
        BrandPojo brandPojo= brandService.getBrandPojo(f.getBrand(),f.getCategory());
        p.setBrandPojo(brandDao.getIdFromBrandCategory(f.getBrand(),f.getCategory()).get(0));
        return p;
    }
}
