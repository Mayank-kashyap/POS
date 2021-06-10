package pos.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;


public class OrderServiceTest extends AbstractUnitTest{

    //declare all pojo beforehand
    @Before
    public void Declaration() throws ApiException {
        declare();
    }

    //testing addition of order
    @Test
    public void testAdd() throws ApiException {

        OrderItemPojo orderItemPojo = getOrderItemPojo(productPojoList.get(0), 5, 30.5);
        List<OrderItemPojo> orderItemPojoList3 = new ArrayList<>();
        orderItemPojoList3.add(orderItemPojo);
        List<OrderPojo> orderPojoList= orderService.getAllOrders();
        List<OrderItemPojo> orderItemPojoList = orderService.getAll();
        orderService.add(orderItemPojoList3);
        List<OrderPojo> orderPojoList1 = orderService.getAllOrders();
        List<OrderItemPojo> orderItemPojoList1 = orderService.getAll();

        assertEquals(orderPojoList.size() + 1, orderPojoList1.size());
        assertEquals(orderItemPojoList.size() + 1, orderItemPojoList1.size());
        List<OrderItemPojo> orderItemPojoList2 = orderService.getOrderItems(orderItemPojo.getOrderId());
        assertEquals(orderItemPojoList3.size(), orderItemPojoList2.size());
        assertEquals(orderItemPojo.getOrderId(), orderItemPojoList2.get(0).getOrderId());
        assertEquals(orderItemPojo.getProductId(), orderItemPojoList2.get(0).getProductId());
        assertEquals(orderItemPojo.getQuantity(), orderItemPojoList2.get(0).getQuantity());
        assertEquals(orderItemPojo.getSp(), orderItemPojoList2.get(0).getSp(), 0.001);

    }

    // Testing adding of invalid order
    @Test
    public void testAddWrong() {

        OrderItemPojo orderItemPojo = getWrongOrderItemPojo(productPojoList.get(0));
        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        orderItemPojoList.add(orderItemPojo);

        try {
            orderService.add(orderItemPojoList);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Quantity must be positive");
        }

    }
    // Testing updating of order items
    @Test
    public void testUpdate() throws ApiException {

        OrderItemPojo orderItemPojo= getOrderItemPojo(productPojoList.get(0), 7, 50);
        orderService.update(orderItemPojoList.get(0).getId(), orderItemPojo);
        assertEquals(orderItemPojoList.get(0).getProductId(), orderItemPojo.getProductId());
        assertEquals(orderItemPojoList.get(0).getQuantity(), orderItemPojo.getQuantity());
        assertEquals(orderItemPojoList.get(0).getSp(), orderItemPojo.getSp(), 0.001);
    }

    // Testing getting for order items
    @Test
    public void testGet() throws ApiException {

        OrderItemPojo orderItemPojo = orderService.get(orderItemPojoList.get(0).getId());
        assertEquals(orderItemPojoList.get(0).getOrderId(), orderItemPojo.getOrderId());
        assertEquals(orderItemPojoList.get(0).getProductId(), orderItemPojo.getProductId());
        assertEquals(orderItemPojoList.get(0).getQuantity(), orderItemPojo.getQuantity());
        assertEquals(orderItemPojoList.get(0).getSp(), orderItemPojo.getSp(), 0.001);
    }


    //returns an orderItem pojo
    private OrderItemPojo getOrderItemPojo(ProductPojo productPojo, int quantity, double sp) {
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setProductId(productPojo.getId());
        orderItemPojo.setQuantity(quantity);
        orderItemPojo.setSp(sp);
        return orderItemPojo;
    }



    private OrderItemPojo getWrongOrderItemPojo(ProductPojo productPojo) {
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setProductId(productPojo.getId());
        orderItemPojo.setQuantity(-5);
        orderItemPojo.setSp(30.0);
        return orderItemPojo;
    }
}
