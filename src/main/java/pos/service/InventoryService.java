package pos.service;

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

    @Transactional(rollbackOn = ApiException.class)
    public void add(InventoryPojo inventoryPojo) throws ApiException{
        check(inventoryPojo);
        inventoryDao.insert(inventoryPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional
    public List<InventoryPojo> getAll() {
        return inventoryDao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, InventoryPojo inventoryPojo) throws ApiException {
        check(inventoryPojo);
        InventoryPojo inventoryPojo1 = getCheck(id);
        inventoryPojo1.setQuantity(inventoryPojo.getQuantity());
        inventoryDao.update(id, inventoryPojo1);
    }

    @Transactional
    public InventoryPojo getCheck(int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryDao.select(id);
        if (inventoryPojo == null) {
            throw new ApiException("Inventory with given ID does not exit, id: " + id);
        }
        return inventoryPojo;
    }

    @Transactional
    public void check(InventoryPojo inventoryPojo) throws ApiException {
        if(inventoryPojo.getQuantity()<0){
            throw new ApiException("Quantity cannot be negative");
        }
    }

    @Transactional
    public InventoryPojo getFromProductId(int productId) throws ApiException {
        InventoryPojo inventoryPojo = inventoryDao.getFromProductId(productId);
        if(inventoryPojo == null){
            throw new ApiException("Inventory with given productId does not exit, productId: " + productId);
        }
        return inventoryPojo;
    }

    //todo exception
    @Transactional
    public BrandPojo getBrandFromInventory(InventoryPojo inventoryPojo){
        ProductPojo productPojo= productDao.select(inventoryPojo.getProductId());
        BrandPojo brandPojo= brandDao.select(productPojo.getBrandCategory());
        return brandPojo;
    }
}