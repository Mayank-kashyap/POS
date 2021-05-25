package pos.service;

import org.junit.Before;
import org.junit.Test;
import pos.pojo.InventoryPojo;
import pos.pojo.ProductPojo;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class InventoryServiceTest extends AbstractUnitTest{
    @Before
    public void Declaration() throws ApiException {
        declare();
    }

    @Test
    public void testAdd() throws ApiException {

        InventoryPojo i = getInventoryPojo(productPojoList.get(2));
        List<InventoryPojo> inv_list_before = inventoryService.getAll();
        inventoryService.add(i);
        List<InventoryPojo> inv_list_after = inventoryService.getAll();
        // Number of brand pojos should increase by one
        assertEquals(inv_list_before.size() + 1, inv_list_after.size());
        assertEquals(i.getProductId(), inventoryService.get(i.getId()).getProductId());
        assertEquals(i.getQuantity(), inventoryService.get(i.getId()).getQuantity());

    }



    /* Testing adding of an invalid pojo. Should throw exception */
    @Test()
    public void testAddWrong() throws ApiException {

        InventoryPojo i = getWrongInventoryPojo(productPojoList.get(0));

        try {
            inventoryService.add(i);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Quantity cannot be negative");
        }

    }


    @Test()
    public void testCheckIfExistsId() throws ApiException {

        InventoryPojo db_inventory_pojo = inventoryService.getCheck(inventoryPojoList.get(0).getId());
        assertEquals(inventoryPojoList.get(0).getProductId(), db_inventory_pojo.getProductId());
        assertEquals(inventoryPojoList.get(0).getQuantity(), db_inventory_pojo.getQuantity());
    }

    /* Testing checkifexists with an non-existent id. Should throw exception */
    @Test()
    public void testCheckIfExistsIdWrong() throws ApiException {
        int id = 5;
        try {
            inventoryService.getCheck(5);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Inventory with given ID does not exist, id: " + id);
        }
    }

    /* Testing validate */
    @Test
    public void testValidate() throws ApiException {
        InventoryPojo ip = getInventoryPojo(productPojoList.get(2));
        inventoryService.check(ip);
        assertTrue(ip.getQuantity() > 0);
    }

    /* Testing get by id */
    @Test()
    public void testGetById() throws ApiException {

        InventoryPojo db_inventory_pojo = inventoryService.get(inventoryPojoList.get(0).getId());
        assertEquals(inventoryPojoList.get(0).getProductId(), db_inventory_pojo.getProductId());
        assertEquals(inventoryPojoList.get(0).getQuantity(), db_inventory_pojo.getQuantity());

    }

    /* Testing get by id of a non-existent pojo. Should throw exception */
    @Test()
    public void testGetByIdNotExisting() throws ApiException {

        int id = 5;
        try {
            inventoryService.get(5);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Inventory with given ID does not exist, id: " + id);
        }

    }

    /* Testing get by product id */
    @Test()
    public void testGetByProductId() throws ApiException {

        InventoryPojo db_inventory_pojo = inventoryService.getFromProductId(inventoryPojoList.get(0).getProductId());
        assertEquals(inventoryPojoList.get(0).getProductId(), db_inventory_pojo.getProductId());
        assertEquals(inventoryPojoList.get(0).getQuantity(), db_inventory_pojo.getQuantity());

    }



    private InventoryPojo getInventoryPojo(ProductPojo p) {
        InventoryPojo i = new InventoryPojo();
        i.setProductId(p.getId());
        i.setQuantity(20);
        return i;
    }

    private InventoryPojo getNewInventoryPojo(ProductPojo p) {
        InventoryPojo i = new InventoryPojo();
        i.setProductId(p.getId());
        i.setQuantity(30);
        return i;
    }

    private InventoryPojo getWrongInventoryPojo(ProductPojo p) {
        InventoryPojo i = new InventoryPojo();
        i.setProductId(p.getId());
        i.setQuantity(-5);
        return i;
    }
}
