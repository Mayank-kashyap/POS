package pos.service;

import org.junit.Before;
import org.junit.Test;
import pos.pojo.BrandPojo;
import pos.pojo.ProductPojo;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProductServiceTest extends AbstractUnitTest{

    //declaration of all pojo beforehand
    @Before
    public void Declaration() throws ApiException {
        declare();
    }

    // Testing adding of product
    @Test
    public void testAdd() throws ApiException {

        BrandPojo brandPojo = brandPojoList.get(0);
        ProductPojo productPojo = getProductDetailsPojo(brandPojo);
        List<ProductPojo> productPojoList = productService.getAll();
        productService.add(productPojo);
        List<ProductPojo> productPojoList1 = productService.getAll();
        assertEquals(productPojoList.size() + 1, productPojoList1.size());
        assertEquals(productPojo.getBarcode(), productService.get(productPojo.getId()).getBarcode());
        assertEquals(productPojo.getName(), productService.get(productPojo.getId()).getName());
        assertEquals(productPojo.getMrp(), productService.get(productPojo.getId()).getMrp(), 0.001);
        assertEquals(productPojo.getBrandCategory(), productService.get(productPojo.getId()).getBrandCategory());

    }

    // Testing adding of an invalid pojo. Should throw an exception
    @Test()
    public void testAddWrong() {

        BrandPojo brandPojo = brandPojoList.get(0);
        ProductPojo productPojo = getWrongProductDetailsPojo(brandPojo);
        try {
            productService.add(productPojo);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Barcode cannot be empty");
        }

    }


    // Testing get by id
    @Test()
    public void testGetById() throws ApiException {

        ProductPojo productPojo = productService.get(productPojoList.get(0).getId());
        assertEquals(productPojoList.get(0).getBarcode(), productPojo.getBarcode());
        assertEquals(productPojoList.get(0).getBrandCategory(), productPojo.getBrandCategory());
        assertEquals(productPojoList.get(0).getMrp(), productPojo.getMrp(), 0.001);
        assertEquals(productPojoList.get(0).getName(), productPojo.getName());

    }

    // Testing get by id for a non-existent pojo. Should throw an exception
    @Test()
    public void testGetByIdNotExisting() {
        try {
            productService.get(100);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Product with given ID does not exist, id: " + 100);
        }

    }

    //returns new product pojo
    private ProductPojo getProductDetailsPojo(BrandPojo brandPojo) {
        ProductPojo productPojo = new ProductPojo();
        productPojo.setBrandCategory(brandPojo.getId());
        productPojo.setName("Milk");
        productPojo.setMrp(50.0);
        productPojo.setBarcode("1Milk1");
        return productPojo;
    }

    //returns new product pojo with invalid entry
    private ProductPojo getWrongProductDetailsPojo(BrandPojo brandPojo) {
        ProductPojo productPojo = new ProductPojo();
        productPojo.setBrandCategory(brandPojo.getId());
        productPojo.setName("");
        productPojo.setMrp(-5.0);
        productPojo.setBarcode("");
        return productPojo;
    }
}