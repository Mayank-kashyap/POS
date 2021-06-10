package pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.ProductDao;
import pos.pojo.ProductPojo;
import pos.util.StringUtil;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    //adds a product
    @Transactional(rollbackOn = ApiException.class)
    public void add(ProductPojo productPojo) throws ApiException{
        normalize(productPojo);
        check(productPojo);
        ProductPojo productPojo1= productDao.getIdFromBarcode(productPojo.getBarcode());
        if(productPojo1!=null) {
            throw new ApiException("Product with given barcode already exists: " + productPojo.getBarcode());
        }
        productPojo.setMrp(Math.round(productPojo.getMrp()*100.0)/100.0);
        productDao.insert(productPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void addList(List<ProductPojo> productPojoList) throws ApiException {
        for (ProductPojo productPojo:productPojoList){
            normalize(productPojo);
            check(productPojo);
        }
        for (ProductPojo productPojo:productPojoList){
            add(productPojo);
        }
    }
    //gets a product by id
    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    //gets product fro barcode
    @Transactional
    public ProductPojo getFromBarcode(String barcode) throws ApiException {
        return checkBarcode(barcode);
    }

    //gets list of all product pojo
    @Transactional
    public List<ProductPojo> getAll() {
        return productDao.selectAll();
    }


    //updates product pojo
    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, ProductPojo productPojo) throws ApiException {
        check(productPojo);
        normalize(productPojo);
        ProductPojo productPojo1 = getCheck(id);
        productPojo1.setBarcode(productPojo.getBarcode());
        productPojo1.setName(productPojo.getName());
        productPojo1.setMrp(Math.round(productPojo.getMrp()*100.0)/100.0);
        productDao.update(id, productPojo1);
    }


    //HELPER METHODS
    //checks whether product pojo is valid or not
    public void check(ProductPojo productPojo) throws ApiException {
        if(StringUtil.isEmpty(productPojo.getBarcode())) {
            throw new ApiException("Barcode cannot be empty");
        }
        if(StringUtil.isEmpty(productPojo.getName())) {
            throw new ApiException("Name cannot be empty");
        }
        if(productPojo.getMrp()<=0)
            throw new ApiException("Mrp cannot be negative or zero");

    }

    //checks whether barcode is valid
    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo checkBarcode(String barcode) throws ApiException {
        if(barcode==null)
            throw new ApiException("Barcode cannot be empty");
        ProductPojo productPojo= productDao.getIdFromBarcode(barcode);
        if(productPojo==null){
            throw new ApiException("Product with given barcode does not exist: "+ barcode);
        }
        return productPojo;
    }

    //checks whether product with given id exists
    @Transactional
    public ProductPojo getCheck(int id) throws ApiException {
        ProductPojo productPojo = productDao.select(id);
        if (productPojo == null) {
            throw new ApiException("Product with given ID does not exist, id: " + id);
        }
        return productPojo;
    }

    //maps all the product pojo with their barcode
    @Transactional
    public Map<String, ProductPojo> getAllProductPojosByBarcode() {
        List<ProductPojo> productPojoList = getAll();
        Map<String, ProductPojo> barcodeProduct = new HashMap<>();
        for (ProductPojo productPojo : productPojoList) {
            barcodeProduct.put(productPojo.getBarcode(), productPojo);
        }
        return barcodeProduct;
    }

    //normalize product pojo
    @Transactional
    protected static void normalize(ProductPojo productPojo) {
        productPojo.setName(StringUtil.toLowerCase(productPojo.getName()));
        productPojo.setBarcode(StringUtil.toLowerCase(productPojo.getBarcode()));
    }

}
