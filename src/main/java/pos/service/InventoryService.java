package pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.InventoryDao;
import pos.dao.ProductDao;
import pos.model.InventoryData;
import pos.model.InventoryForm;
import pos.pojo.InventoryPojo;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private ProductDao productDao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(InventoryPojo inventoryPojo) throws ApiException{
        if(inventoryPojo.getQuantity()<0){
            throw new ApiException("Quantity cannot be negative");
        }
        if(inventoryPojo.getProduct().getBarcode()==null)
        {
            throw new ApiException("Product does not exist");
        }
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
        InventoryPojo ex = getCheck(id);
        ex.setQuantity(inventoryPojo.getQuantity());
        inventoryDao.update(id,ex);
    }

    @Transactional
    public InventoryPojo getCheck(int id) throws ApiException {
        InventoryPojo p = inventoryDao.select(id);
        if (p == null) {
            throw new ApiException("Inventory with given ID does not exit, id: " + id);
        }
        return p;
    }

    @Transactional
    public void check(InventoryPojo inventoryPojo) throws ApiException {
        if(inventoryPojo.getQuantity()<0){
            throw new ApiException("Quantity cannot be negative");
        }
        if(inventoryPojo.getProduct().getBarcode()==null)
        {
            throw new ApiException("Product does not exist");
        }
    }
    @Transactional
    public InventoryPojo convert(InventoryForm inventoryForm) {
        InventoryPojo inventoryPojo=new InventoryPojo();
       inventoryPojo.setId(productDao.getIdFromBarcode(inventoryForm.getBarcode()).getId());
       inventoryPojo.setQuantity(inventoryForm.getQuantity());
        return inventoryPojo;
    }

    @Transactional
    public InventoryData convert(InventoryPojo inventoryPojo) {
        InventoryData inventoryData =new InventoryData();
        inventoryData.setId(inventoryPojo.getId());
        inventoryData.setQuantity(inventoryPojo.getQuantity());
        inventoryData.setBarcode(productDao.select(inventoryPojo.getId()).getBarcode());
        return inventoryData;
    }

}
