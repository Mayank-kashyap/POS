package pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.model.OrderData;
import pos.model.OrderItemData;
import pos.model.OrderItemForm;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;
import pos.service.ApiException;
import pos.service.OrderService;
import pos.service.ProductService;
import pos.util.DataConversionUtil;

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
    
    //Adds an order
    @ApiOperation(value = "Adds Order Details")
    @RequestMapping(path = "/api/order", method = RequestMethod.POST)
    public OrderData add(@RequestBody OrderItemForm[] orderItemForms) throws ApiException{
        Map<String, ProductPojo> allProductPojoByBarcode = productService.getAllProductPojosByBarcode();
        List<OrderItemPojo> orderItemList = DataConversionUtil.convertOrderItemForms(allProductPojoByBarcode, orderItemForms);
        int orderId = orderService.add(orderItemList);
        return DataConversionUtil.convert(orderService.getOrder(orderId));
    }

    //Adds an OrderItem to an existing order
    @ApiOperation(value = "Adds an OrderItem to an existing order")
    @RequestMapping(path = "/api/order_item/{orderId}", method = RequestMethod.POST)
    public void addOrderItem(@PathVariable int orderId, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        orderItemForm.setBarcode(orderItemForm.getBarcode().toLowerCase().trim());
        ProductPojo productPojo = productService.getFromBarcode(orderItemForm.getBarcode());
        OrderItemPojo orderItemPojo = DataConversionUtil.convert(productPojo, orderItemForm);
        orderService.addOrderItem(orderId, orderItemPojo);
    }

    //Gets a OrderItem details record by id
    @ApiOperation(value = "Gets a OrderItem details record by id")
    @RequestMapping(path = "/api/order_item/{id}", method = RequestMethod.GET)
    public OrderItemData get(@PathVariable int id) throws ApiException {
        OrderItemPojo orderItemPojo = orderService.get(id);
        ProductPojo productPojo= productService.get(orderItemPojo.getProductId());
        return DataConversionUtil.convert(orderItemPojo,productPojo);
    }

    //Gets list of Order Items
    @ApiOperation(value = "Gets list of Order Items")
    @RequestMapping(path = "/api/order_item", method = RequestMethod.GET)
    public List<OrderItemData> getAll() throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderService.getAll();
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            ProductPojo productPojo= productService.get(orderItemPojo.getProductId());
            orderItemDataList.add(DataConversionUtil.convert(orderItemPojo,productPojo));
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
            orderDataList.add(DataConversionUtil.convert(orderPojo));
        }
        return orderDataList;
    }

    //Get single order
    @ApiOperation(value = "Gets list of Orders")
    @RequestMapping(path = "/api/singleOrder/{id}", method = RequestMethod.GET)
    public OrderData getSingleOrder(@PathVariable int id) throws ApiException {
        OrderPojo orderPojo= orderService.getOrder(id);
        return DataConversionUtil.convert(orderPojo);
    }

    //Gets list of Order Items of a particular order
    @ApiOperation(value = "Gets list of Order Items of a particular order")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.GET)
    public List<OrderItemData> getOrderItemsByOrderId(@PathVariable int id) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderService.getOrderItems(id);
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            ProductPojo productPojo= productService.get(orderItemPojo.getProductId());
            orderItemDataList.add(DataConversionUtil.convert(orderItemPojo,productPojo));
        }
        return orderItemDataList;
    }

    //Updates a OrderItem record
    @ApiOperation(value = "Updates a OrderItem record")
    @RequestMapping(path = "/api/order_item/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable int id, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        orderItemForm.setBarcode(orderItemForm.getBarcode().toLowerCase().trim());
        ProductPojo productPojo = productService.getFromBarcode(orderItemForm.getBarcode());
        OrderItemPojo orderItemPojo = DataConversionUtil.convert(productPojo, orderItemForm);
        orderService.update(id, orderItemPojo);
    }
}
