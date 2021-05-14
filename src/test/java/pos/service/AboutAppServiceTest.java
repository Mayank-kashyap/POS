package pos.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class AboutAppServiceTest extends AbstractUnitTest{
    @Autowired
    private AboutAppService service;

    @Test
    public void testServiceApis() {
        assertEquals("Point of sale", service.getName());
        assertEquals("1.0", service.getVersion());
    }

}
