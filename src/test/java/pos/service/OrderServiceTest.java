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

    @Before
    public void Declaration() throws ApiException {
        declare();
    }

    @Test
    public void testAdd() throws ApiException {

        OrderItemPojo order_item = getOrderItemPojo(productPojoList.get(0), 5, 30.5);
        List<OrderItemPojo> lis = new ArrayList<OrderItemPojo>();
        lis.add(order_item);
        List<OrderPojo> order_list_before = orderService.getAllOrders();
        List<OrderItemPojo> orderitem_list_before = orderService.getAll();
        orderService.add(lis);
        List<OrderPojo> order_list_after = orderService.getAllOrders();
        List<OrderItemPojo> orderitem_list_after = orderService.getAll();

        assertEquals(order_list_before.size() + 1, order_list_after.size());
        assertEquals(orderitem_list_before.size() + 1, orderitem_list_after.size());
        List<OrderItemPojo> db_orderitem_list = orderService.getOrderItems(order_item.getOrderId());
        assertEquals(lis.size(), db_orderitem_list.size());
        assertEquals(order_item.getOrderId(), db_orderitem_list.get(0).getOrderId());
        assertEquals(order_item.getProductId(), db_orderitem_list.get(0).getProductId());
        assertEquals(order_item.getQuantity(), db_orderitem_list.get(0).getQuantity());
        assertEquals(order_item.getSp(), db_orderitem_list.get(0).getSp(), 0.001);

    }

    /* Testing adding of invalid order. Exception should be thrown */
    @Test
    public void testAddWrong() throws ApiException {

        OrderItemPojo order_item = getWrongOrderItemPojo(productPojoList.get(0));
        List<OrderItemPojo> lis = new ArrayList<OrderItemPojo>();
        lis.add(order_item);

        try {
            orderService.add(lis);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Quantity must be positive");
        }

    }
    /* Testing updation of order items */
    @Test
    public void testUpdate() throws ApiException {

        OrderItemPojo new_order_item = getOrderItemPojo(productPojoList.get(0), 7, 50);
        orderService.update(orderItemPojoList.get(0).getId(), new_order_item);
        assertEquals(orderItemPojoList.get(0).getProductId(), new_order_item.getProductId());
        assertEquals(orderItemPojoList.get(0).getQuantity(), new_order_item.getQuantity());
        assertEquals(orderItemPojoList.get(0).getSp(), new_order_item.getSp(), 0.001);
    }

    /* Testing adding of invalid order (with invalid product). Exception should be thrown
    @Test
    public void testAddNullProduct() throws ApiException {

        OrderItemPojo order_item = getOrderItemPojo(null,5, 60);
        List<OrderItemPojo> lis = new ArrayList<OrderItemPojo>();
        lis.add(order_item);

        try {
            orderService.add(lis);
            fail("ApiException did not occur");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Product with this id does not exist");
        }

    }

     */

    /* Testing Get for order items */
    @Test
    public void testGet() throws ApiException {

        OrderItemPojo db_orderitem_pojo = orderService.get(orderItemPojoList.get(0).getId());
        assertEquals(orderItemPojoList.get(0).getOrderId(), db_orderitem_pojo.getOrderId());
        assertEquals(orderItemPojoList.get(0).getProductId(), db_orderitem_pojo.getProductId());
        assertEquals(orderItemPojoList.get(0).getQuantity(), db_orderitem_pojo.getQuantity());
        assertEquals(orderItemPojoList.get(0).getSp(), db_orderitem_pojo.getSp(), 0.001);
    }


    private OrderItemPojo getOrderItemPojo(ProductPojo p, int quantity, double sp) {
        OrderItemPojo order_item = new OrderItemPojo();
        order_item.setProductId(p.getId());
        order_item.setQuantity(quantity);
        order_item.setSp(sp);
        return order_item;
    }



    private OrderItemPojo getWrongOrderItemPojo(ProductPojo p) {
        OrderItemPojo order_item = new OrderItemPojo();
        order_item.setProductId(p.getId());
        order_item.setQuantity(-5);
        order_item.setSp(30.0);
        return order_item;
    }
}
