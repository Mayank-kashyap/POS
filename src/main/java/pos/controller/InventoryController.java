package pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.model.InventoryData;
import pos.model.InventoryForm;
import pos.pojo.InventoryPojo;
import pos.service.ApiException;
import pos.service.InventoryService;

import java.util.ArrayList;
import java.util.List;

//Controls the inventory page of the application
@Api
@RestController
public class InventoryController extends ExceptionHandler{

    @Autowired
    private InventoryService inventoryService;

    //Adds a product to the inventory
    @ApiOperation(value = "Adds a product to inventory")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.POST)
    public void add(@RequestBody InventoryForm inventoryForm) throws ApiException {
        inventoryService.add(inventoryService.convert(inventoryForm));
    }

    //Retrieves a product by id
    @ApiOperation(value = "Get a product inventory by Id")
    @RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.GET)
    public InventoryData get(@PathVariable int id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.get(id);
        return inventoryService.convert(inventoryPojo);
    }

    //Retrieves the total list of products in the inventory
    @ApiOperation(value = "Get list of complete inventory")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.GET)
    public List<InventoryData> getAll() throws ApiException {
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        List<InventoryData> inventoryDataList = new ArrayList<InventoryData>();
        for (InventoryPojo inventoryPojo : inventoryPojoList){
            inventoryDataList.add(inventoryService.convert(inventoryPojo));
        }
        return inventoryDataList;
    }

    //Updates an inventory of a product
    @ApiOperation(value = "Updates an inventory")
    @RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable int id, @RequestBody InventoryForm inventoryForm) throws ApiException {
        inventoryService.update(id, inventoryService.convert(inventoryForm));
    }
}
