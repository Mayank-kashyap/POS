package pos.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pos.pojo.BrandPojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BrandServiceTest extends AbstractUnitTest{

    @Test
    public void testAdd() throws ApiException {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(" Apple ");
        brandPojo.setCategory(" Personal computer ");
        brandService.add(brandPojo);
        BrandPojo brandPojo1=brandService.get(brandPojo.getId());
        assertEquals(brandPojo1.getBrand(),brandPojo1.getBrand());
        assertEquals(brandPojo1.getCategory(),brandPojo1.getCategory());
        BrandPojo brandPojo2=brandDao.getIdFromBrandCategory("apple","personal computer");
    }

    @Test(expected = ApiException.class)
    public void testInvalid() throws ApiException{
        BrandPojo p = new BrandPojo();
        p.setBrand("");
        p.setCategory("");
            brandService.add(p);
    }

    @Test
    public void testNormalize() {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(" Apple ");
        BrandService.normalize(brandPojo);
        assertEquals("apple", brandPojo.getBrand());
    }
}
