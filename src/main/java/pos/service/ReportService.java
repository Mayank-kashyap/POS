package pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.*;
import pos.pojo.BrandPojo;
import pos.pojo.InventoryPojo;
import pos.pojo.OrderItemPojo;
import pos.pojo.ProductPojo;
import pos.util.DataConversionUtil;
import pos.util.PdfConversionUtil;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private InventoryService inventoryService;

    /* General class for generating Pdf Response */
    public byte[] generatePdfResponse(String type, Object... obj) throws Exception {
        if (type.contentEquals("brand")) {
            BrandXmlList brandXmlList = generateBrandList();
            PdfConversionUtil.generateXml(new File("brand.xml"), brandXmlList, BrandXmlList.class);
            return PdfConversionUtil.generatePDF(new File("brand.xml"), new StreamSource("brand.xsl"));
        } else if (type.contentEquals("inventory")) {
            InventoryXmlList inventory_list = generateInventoryList();
            PdfConversionUtil.generateXml(new File("inventory.xml"), inventory_list, InventoryXmlList.class);
            return PdfConversionUtil.generatePDF(new File("inventory.xml"), new StreamSource("inventory.xsl"));
        }
        else if (type.contentEquals("sales")) {

            SaleXmlList sales_data_list = generateSalesList((ReportFilter) obj[0]);
            if(sales_data_list.getSales_list().isEmpty()) {
                throw new ApiException("No sales was done in this date range for this particular brand and category pair");
            }
            PdfConversionUtil.generateXml(new File("sales.xml"), sales_data_list, SaleXmlList.class);
            return PdfConversionUtil.generatePDF(new File("sales.xml"), new StreamSource("sales.xsl"));
        }
        else {
            OrderInvoiceXmlList idl = generateInvoiceList((Integer) obj[0]);
            PdfConversionUtil.generateXml(new File("invoice.xml"), idl, OrderInvoiceXmlList.class);
            return PdfConversionUtil.generatePDF(new File("invoice.xml"), new StreamSource("invoice.xsl"));
        }
    }

    /* Generating brand list for brand report */
    public BrandXmlList generateBrandList() throws Exception {
        List<BrandPojo> brand_pojo_list = brandService.getAll();
        List<BrandData> brand_data_list = DataConversionUtil.convert(brand_pojo_list);
        BrandXmlList brand_list = new BrandXmlList();
        brand_list.setBrand_list(brand_data_list);
        return brand_list;
    }

    /* Generate inventory list for inventory report */
    public InventoryXmlList generateInventoryList() throws Exception {
        List<InventoryPojo> inventory_pojo_list = inventoryService.getAll();
        Map<BrandPojo, Integer> quantityPerBrandPojo = GroupByBrandCategory(inventory_pojo_list);
        return DataConversionUtil.convertInventoryReportList(quantityPerBrandPojo);

    }

    /* Getting inventory per Brand Category */
    private Map<BrandPojo, Integer> GroupByBrandCategory(List<InventoryPojo> inventory_pojo_list) {
        Map<BrandPojo, Integer> map = new HashMap<>();
        for (InventoryPojo inventoryPojo : inventory_pojo_list) {
            map.merge(inventoryService.getBrandFromInventory(inventoryPojo), inventoryPojo.getQuantity(), Integer::sum);
        }
        return map;
    }

    /*Generate sales list for sales report */
    public SaleXmlList generateSalesList(ReportFilter sales_filter) throws Exception {

        List<OrderItemPojo> order_list = orderService.getAll();
        List<OrderItemPojo> filtered_orderitem_list = FilterByDate(sales_filter, order_list);
        Map<BrandPojo, Integer> quantityPerBrandCategory = getMapQuantity(sales_filter, filtered_orderitem_list);
        Map<BrandPojo, Double> revenuePerBrandCategory = getMapRevenue(sales_filter, filtered_orderitem_list);
        return DataConversionUtil.convertSalesList(quantityPerBrandCategory, revenuePerBrandCategory);
    }

    /*Getting order items based on date */
    private List<OrderItemPojo> FilterByDate(ReportFilter sales_filter, List<OrderItemPojo> orderitem_list) throws ApiException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDate = LocalDate.parse(sales_filter.getStartDate(), formatter).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(sales_filter.getEndDate(), formatter).atStartOfDay().plusDays(1);
        List<OrderItemPojo> filtered_date_list = new ArrayList<>();
        for (OrderItemPojo orderitem : orderitem_list) {
            if (((orderService.getOrder(orderitem.getOrderId())).getDatetime().isAfter(startDate)
                    && (orderService.getOrder(orderitem.getOrderId())).getDatetime().isBefore(endDate)) || (orderService.getOrder(orderitem.getOrderId())).getDatetime().isEqual(startDate)
                    && (orderService.getOrder(orderitem.getOrderId())).getDatetime().isEqual(endDate)) {
                filtered_date_list.add(orderitem);
            }
        }
        return filtered_date_list;
    }

    /* Getting quantity sold based on brand category */
    private Map<BrandPojo, Integer> getMapQuantity(ReportFilter sales_filter, List<OrderItemPojo> orderitem_list) throws ApiException {
        Map<BrandPojo, Integer> quantityPerBrandCategory = new HashMap<>();
        for (OrderItemPojo order_item : orderitem_list) {
            if (Equals(orderService.getBrandFromOrderItem(order_item).getBrand(), sales_filter.getBrand())
                    && Equals(orderService.getBrandFromOrderItem(order_item).getCategory(), sales_filter.getCategory())) {
                quantityPerBrandCategory.merge(orderService.getBrandFromOrderItem(order_item), order_item.getQuantity(), Integer::sum);
            }
        }
        return quantityPerBrandCategory;
    }

    /*Getting revenue generated based on brand category */
    private Map<BrandPojo, Double> getMapRevenue(ReportFilter sales_filter, List<OrderItemPojo> orderitem_list) throws ApiException {
        Map<BrandPojo, Double> revenuePerBrandCategory = new HashMap<>();
        for (OrderItemPojo order_item : orderitem_list) {
            if (Equals(orderService.getBrandFromOrderItem(order_item).getBrand(), sales_filter.getBrand())
                    && Equals(orderService.getBrandFromOrderItem(order_item).getCategory(), sales_filter.getCategory())) {
                revenuePerBrandCategory.merge(orderService.getBrandFromOrderItem(order_item), order_item.getRevenue(), Double::sum);
            }
        }
        ;
        return revenuePerBrandCategory;
    }

    /*String equals or empty (used for filtering) */
    private static Boolean Equals(String a, String b) {
        return (a.contentEquals(b) || b.isEmpty());
    }

    public OrderInvoiceXmlList generateInvoiceList(int order_id) throws Exception {
        List<OrderItemPojo> orderItemPojoList = orderService.getOrderItems(order_id);
        Map<OrderItemPojo,ProductPojo> productPojoList=orderService.getProductPojos(orderItemPojoList);
        OrderInvoiceXmlList orderInvoiceXmlList = DataConversionUtil.convertToInvoiceDataList(orderItemPojoList,productPojoList);
        orderInvoiceXmlList.setOrder_id(orderItemPojoList.get(0).getOrderId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        orderInvoiceXmlList.setDatetime(orderService.getOrder(orderItemPojoList.get(0).getOrderId()).getDatetime().format(formatter));
        double total = calculateTotal(orderInvoiceXmlList);
        orderInvoiceXmlList.setTotal(total);
        return orderInvoiceXmlList;
    }

    /*Calculating total cost of order */
    private static double calculateTotal(OrderInvoiceXmlList idl) {
        double total = 0;
        for (OrderInvoiceData i : idl.getInvoicelist()) {
            total += (i.getMrp() * i.getQuantity());
        }
        return total;
    }
}