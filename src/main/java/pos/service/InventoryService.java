package pos.service;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.BrandDao;
import pos.dao.InventoryDao;
import pos.dao.ProductDao;
import pos.pojo.BrandPojo;
import pos.pojo.InventoryPojo;
import pos.pojo.ProductPojo;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private BrandDao brandDao;

    //Adds a product inventory
    @Transactional(rollbackOn = ApiException.class)
    public void add(InventoryPojo inventoryPojo) throws ApiException{
        check(inventoryPojo);
        InventoryPojo inventoryPojo1=inventoryDao.getFromProductId(inventoryPojo.getProductId());
        if(inventoryPojo1!=null)
        {
            inventoryPojo.setQuantity(inventoryPojo.getQuantity()+inventoryPojo1.getQuantity());
            update(inventoryPojo1.getId(),inventoryPojo );
        }
        else
        inventoryDao.insert(inventoryPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void addList(List<InventoryPojo> inventoryPojoList) throws ApiException {
        for (InventoryPojo inventoryPojo:inventoryPojoList){
            check(inventoryPojo);
        }
        for (InventoryPojo inventoryPojo:inventoryPojoList){
            add(inventoryPojo);
        }
    }
    //retrieves a product inventory by id
    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    //retrieves all product inventories
    @Transactional
    public List<InventoryPojo> getAll() {
        return inventoryDao.selectAll();
    }

    //updates existing inventory
    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, InventoryPojo inventoryPojo) throws ApiException {
        check(inventoryPojo);
        InventoryPojo inventoryPojo1 = getCheck(id);
        inventoryPojo1.setQuantity(inventoryPojo.getQuantity());
        inventoryDao.update(id, inventoryPojo1);
    }

    //checks whether inventory exists or not
    @Transactional
    public InventoryPojo getCheck(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryDao.select(id);
        if (inventoryPojo == null) {
            throw new ApiException("Inventory with given ID does not exist, id: " + id);
        }
        return inventoryPojo;
    }

    //checks whether quantity is positive or not
    @Transactional
    public void check(InventoryPojo inventoryPojo) throws ApiException {
        if(inventoryPojo.getQuantity()<0){
            throw new ApiException("Quantity cannot be negative");
        }
    }

    //retrieves inventory from product id
    @Transactional
    public InventoryPojo getFromProductId(int productId) throws ApiException {
        InventoryPojo inventoryPojo = inventoryDao.getFromProductId(productId);
        if(inventoryPojo == null){
            throw new ApiException("Inventory with given productId does not exist, productId: " + productId);
        }
        return inventoryPojo;
    }

    //retrieves brand pojo from inventory pojo
    @Transactional
    public BrandPojo getBrandFromInventory(InventoryPojo inventoryPojo) throws ApiException {
        getCheck(inventoryPojo.getId());
        ProductPojo productPojo= productDao.select(inventoryPojo.getProductId());
        return brandDao.select(productPojo.getBrandCategory());
    }
}