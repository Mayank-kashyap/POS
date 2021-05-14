package pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.model.ProductData;
import pos.model.ProductForm;
import pos.pojo.ProductPojo;
import pos.service.ApiException;
import pos.service.ProductService;


import java.util.ArrayList;
import java.util.List;

//Controls the products page of the application
@Api
@RestController
public class ProductController extends ExceptionHandler{

    @Autowired
    private ProductService productService;

    //Adds a product
    @ApiOperation(value = "Adds a product")
    @RequestMapping(path = "/api/product", method = RequestMethod.POST)
    public void add(@RequestBody ProductForm productForm) throws ApiException {

        productService.add(productService.convert(productForm));
    }

    //Retrieves a product by productId
    @ApiOperation(value = "Get a product by Id")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.GET)
    public ProductData get(@PathVariable int id) throws ApiException {
        ProductPojo productPojo = productService.get(id);
        return productService.convert(productPojo);
    }

    //Retrieves list of all products
    @ApiOperation(value = "Get list of all products")
    @RequestMapping(path = "/api/product", method = RequestMethod.GET)
    public List<ProductData> getAll() throws ApiException {
        List<ProductPojo> productPojoList = productService.getAll();
        List<ProductData> productDataList = new ArrayList<ProductData>();
        for (ProductPojo productPojo : productPojoList){
            productDataList.add(productService.convert(productPojo));
        }
        return productDataList;
    }

    //Updates a product
    @ApiOperation(value = "Updates a product")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable int id, @RequestBody ProductForm productForm) throws ApiException {
        productService.update(id, productService.convert(productForm));
    }
}
