package pos.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.pojo.*;


import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
//An abstract class for all the test classes
public abstract class AbstractUnitTest {

    @Autowired
    protected BrandService brandService;

    @Autowired
    protected ProductService productService;

    @Autowired
    protected InventoryService inventoryService;

    @Autowired
    protected OrderService orderService;


    protected Integer orderId;
    protected List<BrandPojo> brandPojoList;
    protected List<ProductPojo> productPojoList;
    protected List<InventoryPojo> inventoryPojoList;
    protected List<OrderPojo> orderPojoList;
    protected List<OrderItemPojo> orderItemPojoList;

    //Declares all the pojo and assigns values for testing
    protected void declare() throws ApiException {
        brandPojoList = new ArrayList<>();
        productPojoList = new ArrayList<>();
        inventoryPojoList = new ArrayList<>();
        orderPojoList = new ArrayList<>();
        orderItemPojoList = new ArrayList<>();

        for(int i=0; i<2; i++) {
            BrandPojo brand = new BrandPojo();
            brand.setBrand("brand");
            brand.setCategory("category"+i);
            brandService.add(brand);
            brandPojoList.add(brand);

            ProductPojo product = new ProductPojo();
            product.setName("product"+i);
            product.setMrp(50.0);
            product.setBarcode(i+"product"+i);
            product.setBrandCategory(brand.getId());
            productService.add(product);
            productPojoList.add(product);


            InventoryPojo inventory = new InventoryPojo();
            inventory.setQuantity(20);
            inventory.setProductId(product.getId());
            inventoryService.add(inventory);
            inventoryPojoList.add(inventory);
        }

        ProductPojo product = new ProductPojo();
        product.setBrandCategory(brandPojoList.get(0).getId());
        product.setName("product3");
        product.setMrp(50.0);
        product.setBarcode("1product35");
        productService.add(product);
        productPojoList.add(product);

        List<OrderItemPojo> order_item_list = new ArrayList<>();
        for(int i=0; i<2; i++) {
            OrderItemPojo order_item = new OrderItemPojo();
            order_item.setProductId(productPojoList.get(0).getId());
            order_item.setQuantity(2);
            order_item.setSp(1000.0);
            order_item_list.add(order_item);
        }
        orderId = orderService.add(order_item_list);
        orderPojoList.add(orderService.getOrder(orderId));
        orderItemPojoList.addAll(order_item_list);
    }

}