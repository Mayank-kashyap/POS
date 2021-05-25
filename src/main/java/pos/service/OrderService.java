package pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pos.dao.InventoryDao;
import pos.dao.OrderDao;
import pos.dao.OrderItemDao;
import pos.dao.ProductDao;
import pos.pojo.BrandPojo;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;


import java.time.LocalDateTime;
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
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;

    //Adds a new order
    @Transactional(rollbackFor = ApiException.class)
    public int add(List<OrderItemPojo> orderItemPojoList) throws ApiException{
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setDatetime(LocalDateTime.now());
        orderPojo.setIsInvoiceGenerated(false);
        int orderId =orderDao.insert(orderPojo);
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            orderItemPojo.setOrderId(orderId);
            check(orderItemPojo);
            orderItemDao.insert(orderItemPojo);
            updateInventory(orderItemPojo,0);
        }
        return orderId;
    }


    // Adding order item to an existing order
    @Transactional(rollbackFor = ApiException.class)
    public void addOrderItem(int orderId, OrderItemPojo orderItemPojo) throws ApiException {
        check(orderItemPojo);
        OrderPojo orderPojo=orderDao.select(orderId);
        orderItemPojo.setOrderId(orderPojo.getId());
        List<OrderItemPojo> orderItemPojoList =orderItemDao.getFromOrderId(orderId);
        for(OrderItemPojo item: orderItemPojoList) {
            if(productService.get(item.getProductId()).getBarcode().equals(productService.get(orderItemPojo.getProductId()).getBarcode())){
                update(item.getId(), orderItemPojo);
                return;
            }
        }
        updateInventory(orderItemPojo,0);
        orderItemDao.insert(orderItemPojo);
    }

    @Transactional
    public List<OrderItemPojo> getOrderItems(int orderId) throws ApiException {
        OrderPojo orderPojo=checkIfExistsOrder(orderId);
        return orderItemDao.getFromOrderId(orderId);
    }

    // Fetching an Order by id
    @Transactional
    public OrderPojo getOrder(int id) throws ApiException {
        return checkIfExistsOrder(id);
    }

    // Fetching all orders
    @Transactional
    public List<OrderPojo> getAllOrders() {
        return orderDao.selectAll();
    }

    @Transactional
    public OrderItemPojo get(int id) throws ApiException {
        return checkIfExists(id);
    }

    //fetching all order items
    @Transactional
    public List<OrderItemPojo> getAll() {
        return orderItemDao.selectAll();
    }



    @Transactional(rollbackFor = ApiException.class)
    public void update(int id, OrderItemPojo orderItemPojo) throws ApiException {
        check(orderItemPojo);
        checkIfExists(id);
        OrderItemPojo orderItemPojo1 =orderItemDao.select(id);
        if(!productService.get(orderItemPojo.getProductId()).getBarcode().equals(productService.get(orderItemPojo1.getProductId()).getBarcode())) {
            throw new ApiException("Product does not match");
        }
        updateInventory(orderItemPojo, orderItemPojo1.getQuantity());
        orderItemPojo1.setQuantity(orderItemPojo.getQuantity());
        orderItemPojo1.setOrderId(orderItemPojo1.getOrderId());
        orderItemPojo1.setSp(orderItemPojo.getSp());
    }


    //Updates inventory for every added, updated or deleted order
    @Transactional(rollbackFor = ApiException.class)
    protected void updateInventory(OrderItemPojo orderItemPojo, int old_qty) throws ApiException {
        int quantity = orderItemPojo.getQuantity();
        int quantityInInventory;
        try {
            quantityInInventory = inventoryService.getFromProductId(orderItemPojo.getProductId()).getQuantity() + old_qty;
        } catch (Exception e) {
            throw new ApiException("Inventory for this item does not exist " + productService.get(orderItemPojo.getProductId()).getBarcode());
        }
        if (quantity > quantityInInventory) {
            throw new ApiException(
                    "Maximum allowed quantity: "
                            + quantityInInventory);
        }
        inventoryService.getFromProductId(orderItemPojo.getProductId()).setQuantity(quantityInInventory - quantity);
    }

    //checks whether a given orderItem orderItemPojo is valid or not
    public void check(OrderItemPojo orderItemPojo)throws ApiException {
        if(productService.get(orderItemPojo.getProductId())==null) {
            throw new ApiException("Product with this id does not exist");
        }
        if(orderItemPojo.getQuantity()<=0) {
            throw new ApiException("Quantity must be positive");
        }
        if(orderItemPojo.getSp()<=0) {
            throw new ApiException("Selling price must be positive");
        }
    }

    @Transactional(rollbackFor = ApiException.class)
    public OrderItemPojo checkIfExists(int id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemDao.select(id);
        if (orderItemPojo == null) {
            throw new ApiException("OrderItem with given ID does not exist, id: " + id);
        }
        return orderItemPojo;
    }

    //Checks if order with given id exists or not
    @Transactional(rollbackFor = ApiException.class)
    public OrderPojo checkIfExistsOrder(int id) throws ApiException {
        OrderPojo orderPojo = orderDao.select(id);
        if (orderPojo == null) {
            throw new ApiException("Order with given ID does not exist: " + id);
        }
        return orderPojo;
    }

    @Transactional
    public BrandPojo getBrandFromOrderItem(OrderItemPojo orderItemPojo) throws ApiException {
        ProductPojo productPojo= productService.get(orderItemPojo.getProductId());
        return brandService.get(productPojo.getBrandCategory());
    }

    @Transactional
    public Map<OrderItemPojo,ProductPojo> getProductPojos(List<OrderItemPojo> orderItemPojoList) throws ApiException {
        Map<OrderItemPojo,ProductPojo> orderItemPojoProductPojoMap=new HashMap<>();
        for(OrderItemPojo orderItemPojo:orderItemPojoList){
            ProductPojo productPojo= productService.get(orderItemPojo.getProductId());
            orderItemPojoProductPojoMap.put(orderItemPojo,productPojo);
        }
        return orderItemPojoProductPojoMap;
    }
}
