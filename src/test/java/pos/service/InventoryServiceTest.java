package pos.service;

import org.junit.Before;
import org.junit.Test;
import pos.pojo.InventoryPojo;
import pos.pojo.ProductPojo;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class InventoryServiceTest extends AbstractUnitTest{

    @Before
    public void declarations() throws ApiException {
        declare();
    }

    @Test
    public void testAdd() throws ApiException {

        InventoryPojo i = getInventoryPojo(productPojoList.get(2));
        inventoryService.add(i);
        assertEquals(i.getProduct(), inventoryService.get(i.getId()).getProduct());
        assertEquals(i.getQuantity(), inventoryService.get(i.getId()).getQuantity());
    }

    /* Testing adding of an invalid pojo. Should throw exception */
    @Test()
    public void testInvalid() {

        InventoryPojo i = getWrongInventoryPojo(productPojoList.get(0));
        try {
            inventoryService.add(i);
            fail("Api Exception failed");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Inventory quantity should be positive");
        }

    }

    /* Testing deletion of pojo */
    @Test()
    public void testDelete() throws ApiException {

        InventoryPojo i = getInventoryPojo(productPojoList.get(2));
        inventoryService.add(i);
        inventoryService.delete(i.getId());
        try {
            inventoryService.get(i.getId());
            fail("Api Exception failed");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Inventory with given ID does not exist, id: " + i.getId());
        }

    }

    @Test()
    public void testCheckIfExists() throws ApiException {

        InventoryPojo db_inventory_pojo = inventoryService.getCheck(inventoryPojoList.get(0).getId());
        assertEquals(inventoryPojoList.get(0).getProduct(), db_inventory_pojo.getProduct());
        assertEquals(inventoryPojoList.get(0).getQuantity(), db_inventory_pojo.getQuantity());
    }

    /* Testing checkifexists with an non-existent id. Should throw exception */
    @Test()
    public void testInvalidCheck() {
        int id = 5;
        try {
            inventoryService.getCheck(5);
            fail("Api Exception failed");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Inventory with given ID does not exist, id: " + id);
        }
    }

    /* Testing get by id */
    @Test()
    public void testGetById() throws ApiException {

        InventoryPojo db_inventory_pojo = inventoryService.get(inventoryPojoList.get(0).getId());
        assertEquals(inventoryPojoList.get(0).getProduct(), db_inventory_pojo.getProduct());
        assertEquals(inventoryPojoList.get(0).getQuantity(), db_inventory_pojo.getQuantity());
    }

    /* Testing get by id of a non-existent pojo. Should throw exception */
    @Test()
    public void testInvalidGet() {
        int id = 5;
        try {
            inventoryService.get(5);
            fail("Api Exception failed");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Inventory with given ID does not exist, id: " + id);
        }
    }

    private InventoryPojo getInventoryPojo(ProductPojo p) {
        InventoryPojo i = new InventoryPojo();
        i.setProduct(p);
        i.setQuantity(20);
        return i;
    }

    private InventoryPojo getWrongInventoryPojo(ProductPojo p) {
        InventoryPojo i = new InventoryPojo();
        i.setProduct(p);
        i.setQuantity(-5);
        return i;
    }
}

