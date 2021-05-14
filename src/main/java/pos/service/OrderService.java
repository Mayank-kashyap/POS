package pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.InventoryDao;
import pos.dao.OrderDao;
import pos.dao.OrderItemDao;
import pos.dao.ProductDao;
import pos.model.*;
import pos.pojo.InventoryPojo;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private InventoryService inventoryService;

    //Adds a new order
    @org.springframework.transaction.annotation.Transactional(rollbackFor = ApiException.class)
    public int add(List<OrderItemPojo> orderItemPojoList) throws ApiException{
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setDatetime(LocalDateTime.now());
        int order_id=orderDao.insert(orderPojo);
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            orderItemPojo.setOrderPojo(orderDao.select(order_id));
            check(orderItemPojo);
            orderItemDao.insert(orderItemPojo);
            updateInventory(orderItemPojo,0);
        }
        return order_id;
    }


    // Adding order item to an existing order
    @org.springframework.transaction.annotation.Transactional(rollbackFor = ApiException.class)
    public void addOrderItem(int order_id, OrderItemPojo p) throws ApiException {
        check(p);
        OrderPojo pojo=orderDao.select(order_id);
        p.setOrderPojo(pojo);
        List<OrderItemPojo> list=orderItemDao.getFromOrderId(order_id);
        for(OrderItemPojo item: list) {
            if(item.getProduct().getBarcode().equals(p.getProduct().getBarcode())){
                update(item.getId(),p);
                return;
            }
        }
        updateInventory(p,0);
        orderItemDao.insert(p);

    }

    @org.springframework.transaction.annotation.Transactional
    public List<OrderItemPojo> getOrderItems(int order_id) throws ApiException {
        OrderPojo pojo=orderDao.select(order_id);
        List<OrderItemPojo> lis = orderItemDao.getFromOrderId(order_id);
        return lis;
    }

    // Fetching an Order by id
    @org.springframework.transaction.annotation.Transactional
    public OrderPojo getOrder(int id) throws ApiException {
        OrderPojo p = checkIfExistsOrder(id);
        return p;
    }

    // Fetching all orders
    @org.springframework.transaction.annotation.Transactional
    public List<OrderPojo> getAllOrders() {
        return orderDao.selectAll();
    }

    @org.springframework.transaction.annotation.Transactional
    public OrderItemPojo get(int id) throws ApiException {
        OrderItemPojo p = checkIfExists(id);
        return p;
    }

    //fetching all order items
    @Transactional
    public List<OrderItemPojo> getAll() {
        return orderItemDao.selectAll();
    }



    @Transactional
    public OrderPojo getCheck(int id) throws ApiException {
        OrderPojo orderPojo = orderDao.select(id);
        if (orderPojo == null) {
            throw new ApiException("Order with given ID does not exit, id: " + id);
        }
        return orderPojo;
    }


    @org.springframework.transaction.annotation.Transactional(rollbackFor = ApiException.class)
    public void update(int id, OrderItemPojo p) throws ApiException {

        check(p);
        checkIfExists(id);
        OrderItemPojo pojo=orderItemDao.select(id);
        if(!p.getProduct().getBarcode().equals(pojo.getProduct().getBarcode())) {
            throw new ApiException("Product does not match");
        }
        int order_id=pojo.getOrderPojo().getId();
        updateInventory(p,pojo.getQuantity());
        pojo.setQuantity(p.getQuantity());
        pojo.setOrderPojo(pojo.getOrderPojo());

        orderItemDao.delete(id);
        orderItemDao.insert(pojo);
    }

    //delete an order
    //todo check deleted from get
    @Transactional
    public void delete(int id) throws ApiException {
        List<OrderItemPojo> orderItemPojoList= orderItemDao.getFromOrderId(id);
        for (OrderItemPojo orderItemPojo : orderItemPojoList){
            updateInventory(orderItemPojo,inventoryDao.select(orderItemPojo.getProduct().getId()).getQuantity());
            //inventoryDao.select(orderItemPojo.getProduct().getId()).setQuantity(inventoryDao.select(orderItemPojo.getProduct().getId()).getQuantity()+orderItemPojo.getQuantity());
            orderItemDao.delete(orderItemPojo.getId());
        }
        orderDao.delete(id);
    }


    @org.springframework.transaction.annotation.Transactional
    public void deleteOrderItem(int id) {
        int order_id = orderItemDao.select(id).getOrderPojo().getId();
        orderItemDao.delete(id);
        OrderPojo pojo=orderDao.select(order_id);
        List<OrderItemPojo> lis = orderItemDao.getFromOrderId(order_id);
        if (lis.isEmpty()) {
            orderDao.delete(order_id);
        }
    }

    //Updates inventory for every added, updated or deleted order
    @org.springframework.transaction.annotation.Transactional(rollbackFor = ApiException.class)
    protected void updateInventory(OrderItemPojo pojo, int old_qty) throws ApiException {
        int quantity = pojo.getQuantity();
        int quantityInInventory;
        try {
            quantityInInventory = inventoryDao.select(pojo.getProduct().getId()).getQuantity() + old_qty;
        } catch (Exception e) {
            throw new ApiException("Inventory for this item does not exist " + pojo.getProduct().getBarcode());
        }

        if (quantity > quantityInInventory) {
            throw new ApiException(
                    "Maximum allowed quantity: "
                            + quantityInInventory);
        }
        inventoryDao.select(pojo.getProduct().getId()).setQuantity(quantityInInventory - quantity);
    }

    //checks whether a given orderItem pojo is valid or not
    public void check(OrderItemPojo pojo)throws ApiException {
        if(pojo.getProduct()==null) {
            throw new ApiException("Product with this id does not exist");
        }
        if(pojo.getQuantity()<=0) {
            throw new ApiException("Quantity must be positive");
        }
        if(pojo.getSp()<=0) {
            throw new ApiException("Selling price must be positive");
        }
    }

    @org.springframework.transaction.annotation.Transactional(rollbackFor = ApiException.class)
    public OrderItemPojo checkIfExists(int id) throws ApiException {
        OrderItemPojo p = orderItemDao.select(id);
        if (p == null) {
            throw new ApiException("OrderItem with given ID does not exist, id: " + id);
        }
        return p;
    }

    //Checks if order with given id exists or not
    @org.springframework.transaction.annotation.Transactional(rollbackFor = ApiException.class)
    public OrderPojo checkIfExistsOrder(int id) throws ApiException {
        OrderPojo p = orderDao.select(id);
        if (p == null) {
            throw new ApiException("Order with given ID does not exist: " + id);
        }
        return p;
    }

    //Converts orderItem form to orderItem pojo
    @Transactional
    public OrderItemPojo convert(OrderItemForm orderItemForm){
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setProduct(productDao.getIdFromBarcode(orderItemForm.getBarcode()));
        orderItemPojo.setQuantity(orderItemForm.getQuantity());
        orderItemPojo.setSp(orderItemForm.getSp());
        return orderItemPojo;
    }

    //Converts orderPojo to orderData
    @Transactional
    public OrderData convert(OrderPojo orderPojo){
        OrderData orderData = new OrderData();
        orderData.setId(orderPojo.getId());
        orderData.setDatetime(orderPojo.getDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return orderData;
    }

    //converts list of orderItem forms into list of orderItem pojo
    @Transactional
    public static List<OrderItemPojo> convertOrderItemForms(Map<String, ProductPojo> barcode_product,
                                                            OrderItemForm[] forms) throws ApiException {
        List<OrderItemPojo> list2 = new ArrayList<OrderItemPojo>();
        for (OrderItemForm f : forms) {
            list2.add(convert(barcode_product.get(f.getBarcode()), f));
        }
        return list2;
    }

    //converts orderItem form to orderItem pojo
    @Transactional
    public static OrderItemPojo convert(ProductPojo product_pojo, OrderItemForm f) throws ApiException {
        OrderItemPojo p = new OrderItemPojo();
        p.setProduct(product_pojo);
        p.setQuantity(f.getQuantity());
        p.setSp(f.getSp());
        return p;
    }

    @Transactional
    public static OrderItemData convert(OrderItemPojo p) {
        OrderItemData d = new OrderItemData();
        d.setId(p.getId());
        d.setBarcode(p.getProduct().getBarcode());
        d.setQuantity(p.getQuantity());
        d.setOrderId(p.getOrderPojo().getId());
        d.setSp(p.getSp());
        return d;
    }
}
