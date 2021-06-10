package pos.service;

import org.junit.Before;
import org.junit.Test;
import pos.pojo.InventoryPojo;
import pos.pojo.ProductPojo;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class InventoryServiceTest extends AbstractUnitTest{
    //declare beforehand all pojo
    @Before
    public void Declaration() throws ApiException {
        declare();
    }

    //testing addition of an inventory
    @Test
    public void testAdd() throws ApiException {

        InventoryPojo inventoryPojo = getInventoryPojo(productPojoList.get(2));
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        inventoryService.add(inventoryPojo);
        List<InventoryPojo> inventoryPojoList1 = inventoryService.getAll();
        assertEquals(inventoryPojoList.size() + 1, inventoryPojoList1.size());
        assertEquals(inventoryPojo.getProductId(), inventoryService.get(inventoryPojo.getId()).getProductId());
        assertEquals(inventoryPojo.getQuantity(), inventoryService.get(inventoryPojo.getId()).getQuantity());

    }



    // Testing addition of an invalid pojo
    @Test()
    public void testAddWrong(){

        InventoryPojo inventoryPojo = getWrongInventoryPojo(productPojoList.get(0));

        try {
            inventoryService.add(inventoryPojo);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Quantity cannot be negative");
        }

    }


    //testing whether inventory with id exists or not
    @Test()
    public void testCheckIfExistsId() throws ApiException {

        InventoryPojo inventoryPojo = inventoryService.getCheck(inventoryPojoList.get(0).getId());
        assertEquals(inventoryPojoList.get(0).getProductId(), inventoryPojo.getProductId());
        assertEquals(inventoryPojoList.get(0).getQuantity(), inventoryPojo.getQuantity());
    }

    // Testing inventory with an id which does not exist
    @Test()
    public void testCheckIfExistsIdWrong() {
        int id = 5;
        try {
            inventoryService.getCheck(5);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Inventory with given ID does not exist, id: " + id);
        }
    }

    // Testing validation function
    @Test
    public void testValidate() throws ApiException {
        InventoryPojo inventoryPojo = getInventoryPojo(productPojoList.get(2));
        inventoryService.check(inventoryPojo);
        assertTrue(inventoryPojo.getQuantity() > 0);
    }

    // Testing get by id
    @Test()
    public void testGetById() throws ApiException {

        InventoryPojo inventoryPojo = inventoryService.get(inventoryPojoList.get(0).getId());
        assertEquals(inventoryPojoList.get(0).getProductId(), inventoryPojo.getProductId());
        assertEquals(inventoryPojoList.get(0).getQuantity(), inventoryPojo.getQuantity());

    }

    // Testing get for a pojo which does not exist
    @Test()
    public void testGetByIdNotExisting() {

        int id = 5;
        try {
            inventoryService.get(5);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Inventory with given ID does not exist, id: " + id);
        }

    }

    // Testing get by product id
    @Test()
    public void testGetByProductId() throws ApiException {

        InventoryPojo inventoryPojo = inventoryService.getFromProductId(inventoryPojoList.get(0).getProductId());
        assertEquals(inventoryPojoList.get(0).getProductId(), inventoryPojo.getProductId());
        assertEquals(inventoryPojoList.get(0).getQuantity(), inventoryPojo.getQuantity());

    }

    //returns an inventory
    private InventoryPojo getInventoryPojo(ProductPojo productPojo) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productPojo.getId());
        inventoryPojo.setQuantity(20);
        return inventoryPojo;
    }

    // returns an inventory with error
    private InventoryPojo getWrongInventoryPojo(ProductPojo productPojo) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productPojo.getId());
        inventoryPojo.setQuantity(-5);
        return inventoryPojo;
    }
}
