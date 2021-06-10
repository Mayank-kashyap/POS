package pos.service;

import org.junit.Test;
import pos.pojo.BrandPojo;

import static org.junit.Assert.assertEquals;

public class BrandServiceTest extends AbstractUnitTest{

    //test the addition of a brand
    @Test
    public void testAdd() throws ApiException {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(" Apple ");
        brandPojo.setCategory(" Personal computer ");
        brandService.add(brandPojo);
        BrandPojo brandPojo1=brandService.get(brandPojo.getId());
        assertEquals(brandPojo1.getBrand(),brandPojo1.getBrand());
        assertEquals(brandPojo1.getCategory(),brandPojo1.getCategory());
    }

    //test the exception thrown is correct or not
    @Test(expected = ApiException.class)
    public void testInvalid() throws ApiException{
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand("");
        brandPojo.setCategory("");
        brandService.add(brandPojo);
    }

    //test the normalization function
    @Test
    public void testNormalize() {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(" Apple ");
        BrandService.normalize(brandPojo);
        assertEquals("apple", brandPojo.getBrand());
    }
}