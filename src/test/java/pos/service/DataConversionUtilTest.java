package pos.service;

import org.junit.Before;
import org.junit.Test;
import pos.model.*;
import pos.pojo.*;
import pos.util.DataConversionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DataConversionUtilTest extends AbstractUnitTest{

    @Before
    public void Declaration() throws ApiException {
        declare();
    }

    /* Testing conversion of brand form to pojo */
    @Test
    public void testConvertBrandFormToPojo() {

        BrandForm form = new BrandForm();
        form.setBrand("amul");
        form.setCategory("milk");
        BrandPojo brand_pojo = DataConversionUtil.convert(form);
        assertEquals(form.getBrand(), brand_pojo.getBrand());
        assertEquals(form.getCategory(), brand_pojo.getCategory());
    }

    /* Testing conversion of brand pojo to data */
    @Test
    public void testConvertBrandPojoToData() {

        BrandPojo pojo = new BrandPojo();
        pojo.setId(1);
        pojo.setBrand("amul");
        pojo.setCategory("dairy");
        BrandData brand_data = DataConversionUtil.convert(pojo);
        assertEquals(pojo.getBrand(), brand_data.getBrand());
        assertEquals(pojo.getCategory(), brand_data.getCategory());
    }

    /* Testing conversion of product form to pojo */
    @Test
    public void testConvertProductFormToPojo() throws ApiException {

        ProductForm form = new ProductForm();
        form.setBrand("brand");
        form.setCategory("category0");
        form.setMrp(50.0);
        form.setName("milk");
        ProductPojo product_pojo = DataConversionUtil.convert( form, brandPojoList.get(0));
        assertEquals(form.getBrand(), brandService.get(product_pojo.getBrandCategory()).getBrand());
        assertEquals(form.getCategory(), brandService.get(product_pojo.getBrandCategory()).getCategory());
        assertEquals(form.getName(), product_pojo.getName());
        assertEquals(form.getMrp(), product_pojo.getMrp(), 0.001);
    }

    /* Testing conversion of product pojo to data */
    @Test
    public void testConvertProductPojoToData() throws ApiException {

        ProductPojo product_pojo = new ProductPojo();
        product_pojo.setBarcode("abcdefgh");
        product_pojo.setBrandCategory(brandPojoList.get(0).getId());
        product_pojo.setMrp(50.0);
        product_pojo.setName("milk");
        ProductData product_data = DataConversionUtil.convert(product_pojo,brandPojoList.get(0));
        assertEquals(product_data.getBarcode(), product_pojo.getBarcode());
        assertEquals(product_data.getBrand(), brandService.get(product_pojo.getBrandCategory()).getBrand());
        assertEquals(product_data.getCategory(), brandService.get(product_pojo.getBrandCategory()).getCategory());
        assertEquals(product_data.getName(), product_pojo.getName());
        assertEquals(product_data.getMrp(), product_pojo.getMrp(), 0.001);
    }

    /* Testing conversion of inventory form to pojo */
    @Test
    public void testConvertInventoryFormToPojo() throws ApiException {

        InventoryForm form = new InventoryForm();
        form.setBarcode(productPojoList.get(0).getBarcode());
        form.setQuantity(20);
        InventoryPojo inventory_pojo = DataConversionUtil.convert(form, productPojoList.get(0));
        assertEquals(form.getQuantity(), inventory_pojo.getQuantity());
    }

    /* Testing conversion of inventory pojo to data */
    @Test
    public void testConvertInventoryPojoToData() throws ApiException {

        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(productPojoList.get(0).getId());
        pojo.setQuantity(20);
        InventoryData inventory_data = DataConversionUtil.convert(pojo,productPojoList.get(0));
        assertEquals(inventory_data.getBarcode(), productService.get(pojo.getProductId()).getBarcode());
        assertEquals(inventory_data.getQuantity(), pojo.getQuantity());
    }

    /* Testing conversion of orderitem form to pojo */
    @Test
    public void testConvertOrderItemFormToPojo() throws ApiException {

        OrderItemForm form = new OrderItemForm();
        form.setBarcode(productPojoList.get(0).getBarcode());
        form.setQuantity(2);
        OrderItemPojo pojo = DataConversionUtil.convert(productPojoList.get(0), form);
        assertEquals(form.getBarcode(), productService.get(pojo.getProductId()).getBarcode());
        assertEquals(form.getQuantity(), pojo.getQuantity());
    }

    /* Testing conversion of orderitem pojo to data */
    @Test
    public void testConvertOrderItemPojoToData() throws ApiException {

        OrderItemPojo pojo = new OrderItemPojo();
        pojo.setProductId(productPojoList.get(0).getId());
        pojo.setOrderId(orderId);
        pojo.setQuantity(2);
        pojo.setSp(30.0);
        OrderItemData data = DataConversionUtil.convert(pojo,productPojoList.get(0));
        assertEquals(data.getBarcode(), productService.get(pojo.getProductId()).getBarcode());
        assertEquals(data.getOrderId(), pojo.getOrderId());
        assertEquals(data.getQuantity(), pojo.getQuantity());
    }

    /* Testing conversion of list of brand pojos to data */
    @Test
    public void testListBrandPojoToData() {
        List<BrandPojo> brand_list = brandService.getAll();
        List<BrandData> brand_data_list = DataConversionUtil.convert(brand_list);
        assertEquals(brand_list.size(), brand_data_list.size());
        assertEquals(brand_list.get(0).getBrand(), brand_data_list.get(0).getBrand());
        assertEquals(brand_list.get(0).getCategory(), brand_data_list.get(0).getCategory());
    }


    /* Testing conversion of list of order items to invoice */
    @Test
    public void testOrderItemstoInvoice() throws ApiException {
        List<OrderItemPojo> order_item_list = orderService.getAll();
        Map<OrderItemPojo,ProductPojo> productPojoList=orderService.getProductPojos(orderItemPojoList);
        OrderInvoiceXmlList invoice_list = DataConversionUtil.convertToInvoiceDataList(order_item_list,productPojoList);
        assertEquals(order_item_list.size(), invoice_list.getOrderInvoiceData().size());
        assertEquals(invoice_list.getOrderInvoiceData().get(0).getName(), productService.get(order_item_list.get(0).getProductId()).getName());
        assertEquals(invoice_list.getOrderInvoiceData().get(0).getQuantity(), order_item_list.get(0).getQuantity());
        assertEquals(invoice_list.getOrderInvoiceData().get(0).getMrp(), order_item_list.get(0).getSp(), 0.001);
    }


    /* Testing conversion of list of orderitem forms to pojos */
    @Test
    public void testOrderItemsFormtoPojo() throws ApiException {
        OrderItemForm[] order_item_forms = new OrderItemForm[1];
        OrderItemForm form1 = new OrderItemForm();
        form1.setBarcode(productPojoList.get(0).getBarcode());
        form1.setQuantity(2);
        order_item_forms[0] = form1;
        Map<String, ProductPojo> barcode_product = new HashMap<String, ProductPojo>();
        barcode_product.put(productPojoList.get(0).getBarcode(), productPojoList.get(0));
        barcode_product.put(productPojoList.get(1).getBarcode(), productPojoList.get(1));

        List<OrderItemPojo> pojo_list = DataConversionUtil.convertOrderItemForms(barcode_product, order_item_forms);
        assertEquals(1, pojo_list.size());
        assertEquals(order_item_forms[0].getBarcode(), productService.get(pojo_list.get(0).getProductId()).getBarcode());
        assertEquals(order_item_forms[0].getQuantity(), pojo_list.get(0).getQuantity());
    }

    /* Testing conversion of order pojo to data */
    @Test
    public void testConvertOrderPojoToData() throws ApiException {
        OrderPojo pojo = orderService.getOrder(orderId);
        OrderData order_data = DataConversionUtil.convert(pojo);
        assertEquals(pojo.getId(), order_data.getId());
    }

    /* Test conversion to inventory report list */
    @Test
    public void testConvertInventoryReportList() throws ApiException {
        Map<BrandPojo, Integer> quantityPerBrandPojo = new HashMap<BrandPojo, Integer>();
        BrandPojo b1 = new BrandPojo();
        b1.setBrand("brand1");
        b1.setCategory("category1");
        BrandPojo b2 = new BrandPojo();
        b2.setBrand("brand2");
        b2.setCategory("category2");
        quantityPerBrandPojo.put(b1, 1);
        quantityPerBrandPojo.put(b2, 2);
        InventoryXmlList inv_list = DataConversionUtil.convertInventoryReportList(quantityPerBrandPojo);
        assertEquals(2, inv_list.getInventoryReportData().size());
    }

    /* Test conversion to sales list */
    @Test
    public void testConvertSalesList() {
        Map<BrandPojo, Integer> quantityPerBrandPojo = new HashMap<BrandPojo, Integer>();
        Map<BrandPojo, Double> revenuePerBrandCategory = new HashMap<BrandPojo, Double>();
        BrandPojo b1 = new BrandPojo();
        b1.setBrand("brand1");
        b1.setCategory("category1");
        BrandPojo b2 = new BrandPojo();
        b2.setBrand("brand2");
        b2.setCategory("category2");
        quantityPerBrandPojo.put(b1, 1);
        quantityPerBrandPojo.put(b2, 2);
        revenuePerBrandCategory.put(b1, 100.00);
        revenuePerBrandCategory.put(b2, 200.00);
        SaleXmlList saleXmlList = DataConversionUtil.convertSalesList(quantityPerBrandPojo, revenuePerBrandCategory);
        assertEquals(2, saleXmlList.getSaleReportDataList().size());
    }
}
