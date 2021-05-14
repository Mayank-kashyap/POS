package pos.util;

import pos.model.*;
import pos.pojo.BrandPojo;
import pos.pojo.OrderItemPojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataConversionUtil {

    //Convert Map of quantity per BrandPojo to inventory list
    public static InventoryXmlList convertInventoryReportList(Map<BrandPojo, Integer> quantityPerBrandPojo) {
        List<InventoryReportData> inventory_report_list = new ArrayList<InventoryReportData>();
        for (BrandPojo brand_pojo : quantityPerBrandPojo.keySet()) {
            InventoryReportData d = new InventoryReportData();
            d.setBrand(brand_pojo.getBrand());
            d.setCategory(brand_pojo.getCategory());
            d.setQuantity(quantityPerBrandPojo.get(brand_pojo));
            inventory_report_list.add(d);
        }
        InventoryXmlList inventory_list = new InventoryXmlList();
        inventory_list.setInventory_list(inventory_report_list);
        return inventory_list;
    }

    //Convert Maps of quantity sold and revenue per BrandPojo to sales list
    public static SaleXmlList convertSalesList(Map<BrandPojo, Integer> quantityPerBrandCategory,
                                               Map<BrandPojo, Double> revenuePerBrandCategory) {

        List<SaleReportData> sales_list = new ArrayList<SaleReportData>();
        for(BrandPojo brand: quantityPerBrandCategory.keySet()) {
            SaleReportData sales = new SaleReportData();
            sales.setBrand(brand.getBrand());
            sales.setCategory(brand.getCategory());
            sales.setQuantity(quantityPerBrandCategory.get(brand));
            sales.setRevenue(revenuePerBrandCategory.get(brand));
            sales_list.add(sales);
        }
        SaleXmlList sales_data_list = new SaleXmlList();
        sales_data_list.setSales_list(sales_list);
        return sales_data_list;

    }

    //Convert list of orderitems to invoice list
    public static OrderInvoiceXmlList convertToInvoiceDataList(List<OrderItemPojo> lis) {
        List<OrderInvoiceData> invoiceLis = new ArrayList<OrderInvoiceData>();
        for (OrderItemPojo p : lis) {
            OrderInvoiceData i = new OrderInvoiceData();
            i.setId(p.getId());
            i.setMrp(p.getProduct().getMrp());
            i.setName(p.getProduct().getName());
            i.setQuantity(p.getQuantity());
            invoiceLis.add(i);
        }
        OrderInvoiceXmlList idl = new OrderInvoiceXmlList();
        idl.setInvoicelist(invoiceLis);
        return idl;
    }
}
