package pos.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pos.pojo.BrandPojo;
import pos.pojo.ProductPojo;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProductServiceTest extends AbstractUnitTest{

    @Before
    public void Declaration() throws ApiException {
        declare();
    }
    /* Testing adding of product details pojo */
    @Test
    public void testAdd() throws ApiException {

        BrandPojo b = brandPojoList.get(0);
        ProductPojo p = getProductDetailsPojo(b);
        List<ProductPojo> product_list_before = productService.getAll();
        productService.add(p);
        List<ProductPojo> product_list_after = productService.getAll();
        assertEquals(product_list_before.size() + 1, product_list_after.size());
        assertEquals(p.getBarcode(), productService.get(p.getId()).getBarcode());
        assertEquals(p.getName(), productService.get(p.getId()).getName());
        assertEquals(p.getMrp(), productService.get(p.getId()).getMrp(), 0.001);
        assertEquals(p.getBrandCategory(), productService.get(p.getId()).getBrandCategory());

    }

    /* Testing adding of an invalid pojo. Should throw an exception */
    @Test()
    public void testAddWrong() throws ApiException {

        BrandPojo b = brandPojoList.get(0);
        ProductPojo p = getWrongProductDetailsPojo(b);
        try {
            productService.add(p);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Barcode cannot be empty");
        }

    }


    /* Testing get by id */
    @Test()
    public void testGetById() throws ApiException {

        ProductPojo db_product_pojo = productService.get(productPojoList.get(0).getId());
        assertEquals(productPojoList.get(0).getBarcode(), db_product_pojo.getBarcode());
        assertEquals(productPojoList.get(0).getBrandCategory(), db_product_pojo.getBrandCategory());
        assertEquals(productPojoList.get(0).getMrp(), db_product_pojo.getMrp(), 0.001);
        assertEquals(productPojoList.get(0).getName(), db_product_pojo.getName());

    }

    /* Testing get by id for a non-existent pojo. Should throw an exception */
    @Test()
    public void testGetByIdNotExisting() throws ApiException {
        try {
            productService.get(100);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Product with given ID does not exist, id: " + 100);
        }

    }

    private ProductPojo getProductDetailsPojo(BrandPojo b) throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBrandCategory(b.getId());
        p.setName("Milk");
        p.setMrp(50.0);
        p.setBarcode("1Milk1");
        return p;
    }

    private ProductPojo getWrongProductDetailsPojo(BrandPojo b) throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBrandCategory(b.getId());
        p.setName("");
        p.setMrp(-5.0);
        p.setBarcode("");
        return p;
    }
}