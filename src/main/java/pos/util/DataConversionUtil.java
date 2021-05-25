package pos.util;

import pos.model.*;
import pos.pojo.*;
import pos.service.ApiException;


import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataConversionUtil {

    //convert brand form into brand pojo
    public static BrandPojo convert(BrandForm brandForm) {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(brandForm.getBrand());
        brandPojo.setCategory(brandForm.getCategory());
        return brandPojo;
    }

    //Converts a brand pojo into brand data
    public static BrandData convert(BrandPojo brandPojo) {
        BrandData brandData = new BrandData();
        brandData.setBrand(brandPojo.getBrand());
        brandData.setCategory(brandPojo.getCategory());
        brandData.setId(brandPojo.getId());
        return brandData;
    }
    //converts List of brand pojo to List of brand data
    public static List<BrandData> convert(List<BrandPojo> brandPojoList) {
        List<BrandData> brandDataList = new ArrayList<>();
        for (BrandPojo brandPojo : brandPojoList) {
            brandDataList.add(convert(brandPojo));
        }
        return brandDataList;
    }

    //converts product form into product pojo
    public static ProductPojo convert(ProductForm productForm, BrandPojo brandPojo) throws ApiException {
        ProductPojo productPojo = new ProductPojo();
        productPojo.setBarcode(productForm.getBarcode());
        productPojo.setName(productForm.getName());
        productPojo.setMrp(productForm.getMrp());
        productPojo.setBrandCategory(brandPojo.getId());
        return productPojo;
    }

    //converts product pojo into product data
    public static ProductData convert(ProductPojo productPojo, BrandPojo brandPojo) throws ApiException {
        ProductData productData = new ProductData();
        productData.setBarcode(productPojo.getBarcode());
        productData.setName(productPojo.getName());
        productData.setMrp(productPojo.getMrp());
        productData.setId(productPojo.getId());
        productData.setBrand(brandPojo.getBrand());
        productData.setCategory(brandPojo.getCategory());
        return productData;
    }

    //converts inventory form into inventory popjo
    public static InventoryPojo convert(InventoryForm inventoryForm, ProductPojo productPojo) throws ApiException {
        InventoryPojo inventoryPojo=new InventoryPojo();
        inventoryPojo.setProductId(productPojo.getId());
        inventoryPojo.setQuantity(inventoryForm.getQuantity());
        return inventoryPojo;
    }

    //converts inventory pojo into inventory data
    public static InventoryData convert(InventoryPojo inventoryPojo, ProductPojo productPojo) {
        InventoryData inventoryData =new InventoryData();
        inventoryData.setId(inventoryPojo.getId());
        inventoryData.setQuantity(inventoryPojo.getQuantity());
        inventoryData.setBarcode(productPojo.getBarcode());
        return inventoryData;
    }

    //converts list of orderItemForms into list of orderItem pojo
    public static List<OrderItemPojo> convertOrderItemForms(Map<String, ProductPojo> barcodeProduct,
                                                            OrderItemForm[] orderItemForms) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        for (OrderItemForm orderItemForm : orderItemForms) {
            orderItemPojoList.add(convert(barcodeProduct.get(orderItemForm.getBarcode()), orderItemForm));
        }
        return orderItemPojoList;
    }

    //converts orderItem form to orderItem pojo
    public static OrderItemPojo convert(ProductPojo productPojo, OrderItemForm orderItemForm) throws ApiException {
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setProductId(productPojo.getId());
        orderItemPojo.setQuantity(orderItemForm.getQuantity());
        orderItemPojo.setSp(orderItemForm.getSp());
        return orderItemPojo;
    }

    //converts order item pojo to order data
    public static OrderItemData convert(OrderItemPojo orderItemPojo, ProductPojo productPojo) throws ApiException {
        OrderItemData orderItemData = new OrderItemData();
        orderItemData.setId(orderItemPojo.getId());
        orderItemData.setBarcode(productPojo.getBarcode());
        orderItemData.setQuantity(orderItemPojo.getQuantity());
        orderItemData.setOrderId(orderItemPojo.getOrderId());
        orderItemData.setSp(orderItemPojo.getSp());
        return orderItemData;
    }
    //Converts orderPojo to orderData
    public static OrderData convert(OrderPojo orderPojo){
        OrderData orderData = new OrderData();
        orderData.setId(orderPojo.getId());
        orderData.setDatetime(orderPojo.getDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return orderData;
    }


    //Convert Map of quantity per BrandPojo to inventory list
    public static InventoryXmlList convertInventoryReportList(Map<BrandPojo, Integer> quantityPerBrandPojo) {
        List<InventoryReportData> inventory_report_list = new ArrayList<>();
        for (BrandPojo brand_pojo : quantityPerBrandPojo.keySet()) {
            InventoryReportData d = new InventoryReportData();
            d.setBrand(brand_pojo.getBrand());
            d.setCategory(brand_pojo.getCategory());
            d.setQuantity(quantityPerBrandPojo.get(brand_pojo));
            inventory_report_list.add(d);
        }
        InventoryXmlList inventory_list = new InventoryXmlList();
        inventory_list.setInventoryReportData(inventory_report_list);
        return inventory_list;
    }

    //Convert Maps of quantity sold and revenue per BrandPojo to sales list
    public static SaleXmlList convertSalesList(Map<BrandPojo, Integer> quantityPerBrandCategory,
                                               Map<BrandPojo, Double> revenuePerBrandCategory) {

        List<SaleReportData> sales_list = new ArrayList<>();
        for(BrandPojo brand: quantityPerBrandCategory.keySet()) {
            SaleReportData sales = new SaleReportData();
            sales.setBrand(brand.getBrand());
            sales.setCategory(brand.getCategory());
            sales.setQuantity(quantityPerBrandCategory.get(brand));
            sales.setRevenue(revenuePerBrandCategory.get(brand));
            sales_list.add(sales);
        }
        SaleXmlList saleXmlList = new SaleXmlList();
        saleXmlList.setSaleReportDataList(sales_list);
        return saleXmlList;

    }

    //Convert list of orderitems to invoice list
    public static OrderInvoiceXmlList convertToInvoiceDataList(List<OrderItemPojo> orderItemPojoList, Map<OrderItemPojo,ProductPojo> productPojoList) {
        List<OrderInvoiceData> orderInvoiceDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            OrderInvoiceData orderInvoiceData = new OrderInvoiceData();
            orderInvoiceData.setId(orderItemPojo.getId());
            orderInvoiceData.setMrp(orderItemPojo.getSp());
            orderInvoiceData.setName(productPojoList.get(orderItemPojo).getName());
            orderInvoiceData.setQuantity(orderItemPojo.getQuantity());
            orderInvoiceDataList.add(orderInvoiceData);
        }
        OrderInvoiceXmlList orderInvoiceXmlList = new OrderInvoiceXmlList();
        orderInvoiceXmlList.setOrderInvoiceData(orderInvoiceDataList);
        return orderInvoiceXmlList;
    }
}
