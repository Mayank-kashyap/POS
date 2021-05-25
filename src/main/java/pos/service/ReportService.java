package pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.*;
import pos.pojo.*;
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
            InventoryXmlList inventoryXmlList = generateInventoryList();
            PdfConversionUtil.generateXml(new File("inventory.xml"), inventoryXmlList, InventoryXmlList.class);
            return PdfConversionUtil.generatePDF(new File("inventory.xml"), new StreamSource("inventory.xsl"));
        }
        else if (type.contentEquals("sales")) {

            SaleXmlList saleXmlList = generateSalesList((ReportFilter) obj[0]);
            if(saleXmlList.getSaleReportDataList().isEmpty()) {
                throw new ApiException("No sales was done in this date range for this particular brand and category pair");
            }
            PdfConversionUtil.generateXml(new File("sales.xml"), saleXmlList, SaleXmlList.class);
            return PdfConversionUtil.generatePDF(new File("sales.xml"), new StreamSource("sales.xsl"));
        }
        else {
            OrderInvoiceXmlList orderInvoiceXmlList = generateInvoiceList((Integer) obj[0]);
            PdfConversionUtil.generateXml(new File("invoice.xml"), orderInvoiceXmlList, OrderInvoiceXmlList.class);
            return PdfConversionUtil.generatePDF(new File("invoice.xml"), new StreamSource("invoice.xsl"));
        }
    }

    /* Generating brand list for brand report */
    public BrandXmlList generateBrandList() {
        List<BrandPojo> brandPojoList = brandService.getAll();
        List<BrandData> brandDataList = DataConversionUtil.convert(brandPojoList);
        BrandXmlList brandXmlList = new BrandXmlList();
        brandXmlList.setBrandDataList(brandDataList);
        return brandXmlList;
    }

    /* Generate inventory list for inventory report */
    public InventoryXmlList generateInventoryList() throws ApiException {
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        Map<BrandPojo, Integer> quantityPerBrandPojo = GroupByBrandCategory(inventoryPojoList);
        return DataConversionUtil.convertInventoryReportList(quantityPerBrandPojo);

    }

    /* Getting inventory per Brand Category */
    private Map<BrandPojo, Integer> GroupByBrandCategory(List<InventoryPojo> inventoryPojoList) throws ApiException {
        Map<BrandPojo, Integer> map = new HashMap<>();
        for (InventoryPojo inventoryPojo : inventoryPojoList) {
            map.merge(inventoryService.getBrandFromInventory(inventoryPojo), inventoryPojo.getQuantity(), Integer::sum);
        }
        return map;
    }

    //
    /*Generate sales list for sales report */
    public SaleXmlList generateSalesList(ReportFilter reportFilter) throws Exception {

        List<OrderItemPojo> orderItemPojoList = orderService.getAll();
        List<OrderItemPojo> orderItemPojoList1 = FilterByDate(reportFilter, orderItemPojoList);
        Map<BrandPojo, Integer> quantityPerBrandCategory = getMapQuantity(reportFilter, orderItemPojoList1);
        Map<BrandPojo, Double> revenuePerBrandCategory = getMapRevenue(reportFilter, orderItemPojoList1);
        return DataConversionUtil.convertSalesList(quantityPerBrandCategory, revenuePerBrandCategory);
    }

    /*Getting order items based on date */
    private List<OrderItemPojo> FilterByDate(ReportFilter reportFilter, List<OrderItemPojo> orderItemPojoList) throws ApiException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDate = LocalDate.parse(reportFilter.getStartDate(), formatter).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(reportFilter.getEndDate(), formatter).atStartOfDay().plusDays(1);
        List<OrderItemPojo> orderItemPojoList1 = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            if (((orderService.getOrder(orderItemPojo.getOrderId())).getDatetime().isAfter(startDate)
                    && (orderService.getOrder(orderItemPojo.getOrderId())).getDatetime().isBefore(endDate)) || (orderService.getOrder(orderItemPojo.getOrderId())).getDatetime().isEqual(startDate)
                    && (orderService.getOrder(orderItemPojo.getOrderId())).getDatetime().isEqual(endDate)) {
                orderItemPojoList1.add(orderItemPojo);
            }
        }
        return orderItemPojoList1;
    }

    /* Getting quantity sold based on brand category */
    private Map<BrandPojo, Integer> getMapQuantity(ReportFilter reportFilter, List<OrderItemPojo> orderItemPojoList) throws ApiException {
        Map<BrandPojo, Integer> quantityPerBrandCategory = new HashMap<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            if (Equals(orderService.getBrandFromOrderItem(orderItemPojo).getBrand(), reportFilter.getBrand())
                    && Equals(orderService.getBrandFromOrderItem(orderItemPojo).getCategory(), reportFilter.getCategory())) {
                quantityPerBrandCategory.merge(orderService.getBrandFromOrderItem(orderItemPojo), orderItemPojo.getQuantity(), Integer::sum);
            }
        }
        return quantityPerBrandCategory;
    }

    /*Getting revenue generated based on brand category */
    private Map<BrandPojo, Double> getMapRevenue(ReportFilter reportFilter, List<OrderItemPojo> orderItemPojoList) throws ApiException {
        Map<BrandPojo, Double> revenuePerBrandCategory = new HashMap<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            if (Equals(orderService.getBrandFromOrderItem(orderItemPojo).getBrand(), reportFilter.getBrand())
                    && Equals(orderService.getBrandFromOrderItem(orderItemPojo).getCategory(), reportFilter.getCategory())) {
                revenuePerBrandCategory.merge(orderService.getBrandFromOrderItem(orderItemPojo), orderItemPojo.getRevenue(), Double::sum);
            }
        }
        return revenuePerBrandCategory;
    }

    /*String equals or empty (used for filtering) */
    private static Boolean Equals(String a, String b) {
        return (a.contentEquals(b) || b.isEmpty());
    }

    //todo check invoice generated
    public OrderInvoiceXmlList generateInvoiceList(int orderId) throws Exception {
        List<OrderItemPojo> orderItemPojoList = orderService.getOrderItems(orderId);
        Map<OrderItemPojo,ProductPojo> productPojoList=orderService.getProductPojos(orderItemPojoList);
        OrderInvoiceXmlList orderInvoiceXmlList = DataConversionUtil.convertToInvoiceDataList(orderItemPojoList,productPojoList);
        orderInvoiceXmlList.setOrder_id(orderItemPojoList.get(0).getOrderId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        orderInvoiceXmlList.setDatetime(orderService.getOrder(orderItemPojoList.get(0).getOrderId()).getDatetime().format(formatter));
        double total = calculateTotal(orderInvoiceXmlList);
        orderInvoiceXmlList.setTotal(total);
        orderService.getOrder(orderId).setIsInvoiceGenerated(true);
        return orderInvoiceXmlList;
    }

    /*Calculating total cost of order */
    private static double calculateTotal(OrderInvoiceXmlList orderInvoiceXmlList) {
        double total = 0;
        for (OrderInvoiceData orderInvoiceData : orderInvoiceXmlList.getOrderInvoiceData()) {
            total += (orderInvoiceData.getMrp() * orderInvoiceData.getQuantity());
        }
        return total;
    }
}