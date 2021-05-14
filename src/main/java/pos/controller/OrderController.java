package pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.dao.ProductDao;
import pos.model.OrderData;
import pos.model.OrderItemData;
import pos.model.OrderItemForm;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;
import pos.service.ApiException;
import pos.service.OrderService;
import pos.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Controls the order page of the application
@Api
@RestController
public class OrderController extends ExceptionHandler{

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDao productDao;

    public OrderController() {
    }


    //Adds an order
    @ApiOperation(value = "Adds Order Details")
    @RequestMapping(path = "/api/order", method = RequestMethod.POST)
    //todo remove response and apiException
    public OrderData add(@RequestBody OrderItemForm[] orderItemForms) throws ApiException{
        Map<String, ProductPojo> barcode_product = productService.getAllProductPojosByBarcode();
        List<OrderItemPojo> orderItemList = OrderService.convertOrderItemForms(barcode_product, orderItemForms);
        int orderId = orderService.add(orderItemList);
        return orderService.convert(orderService.getOrder(orderId));
    }

    //Adds an OrderItem to an existing order
    @ApiOperation(value = "Adds an OrderItem to an existing order")
    @RequestMapping(path = "/api/order_item/{orderId}", method = RequestMethod.POST)
    public void addOrderItem(@PathVariable int orderId, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        ProductPojo productPojo = productDao.getIdFromBarcode(orderItemForm.getBarcode());
        OrderItemPojo orderItemPojo = OrderService.convert(productPojo, orderItemForm);
        orderService.addOrderItem(orderId, orderItemPojo);
    }

    //Gets a OrderItem details record by id
    @ApiOperation(value = "Gets a OrderItem details record by id")
    @RequestMapping(path = "/api/order_item/{id}", method = RequestMethod.GET)
    public OrderItemData get(@PathVariable int id) throws ApiException {
        OrderItemPojo orderItemPojo = orderService.get(id);
        return OrderService.convert(orderItemPojo);
    }

    //Deletes an Order by id
    @ApiOperation(value = "Deletes an Order by id")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.DELETE)
    public void deleteOrder(@PathVariable int id) throws ApiException {
        orderService.delete(id);
    }

    //Gets list of Order Items
    @ApiOperation(value = "Gets list of Order Items")
    @RequestMapping(path = "/api/order_item", method = RequestMethod.GET)
    public List<OrderItemData> getAll() {
        List<OrderItemPojo> orderItemPojoList = orderService.getAll();
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            orderItemDataList.add(OrderService.convert(orderItemPojo));
        }
        return orderItemDataList;
    }

    //Gets list of Orders
    @ApiOperation(value = "Gets list of Orders")
    @RequestMapping(path = "/api/order", method = RequestMethod.GET)
    public List<OrderData> getAllOrders() {
        List<OrderPojo> orderPojoList = orderService.getAllOrders();
        List<OrderData> orderDataList = new ArrayList<>();
        for (OrderPojo orderPojo : orderPojoList) {
            orderDataList.add(orderService.convert(orderPojo));
        }
        return orderDataList;
    }

    //Gets list of Order Items of a particular order
    @ApiOperation(value = "Gets list of Order Items of a particular order")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.GET)
    public List<OrderItemData> getOrderItemsByOrderId(@PathVariable int id) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderService.getOrderItems(id);
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            orderItemDataList.add(OrderService.convert(orderItemPojo));
        }
        return orderItemDataList;
    }

    //Deletes Order Item record
    @ApiOperation(value = "Deletes Order Item record")
    @RequestMapping(path = "/api/order_item/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable int id) {
        orderService.deleteOrderItem(id);
    }

    //Updates a OrderItem record
    @ApiOperation(value = "Updates a OrderItem record")
    @RequestMapping(path = "/api/order_item/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable int id, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        ProductPojo productPojo = productDao.getIdFromBarcode(orderItemForm.getBarcode());
        OrderItemPojo orderItemPojo = OrderService.convert(productPojo, orderItemForm);
        orderService.update(id, orderItemPojo);
    }
}
