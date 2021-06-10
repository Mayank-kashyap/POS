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

    //declare all the pojo beforehand
    @Before
    public void Declaration() throws ApiException {
        declare();
    }

    // Testing conversion of brand form to pojo
    @Test
    public void testConvertBrandFormToPojo() {

        BrandForm brandForm = new BrandForm();
        brandForm.setBrand("amul");
        brandForm.setCategory("milk");
        BrandPojo brandPojo = DataConversionUtil.convert(brandForm);
        assertEquals(brandForm.getBrand(), brandPojo.getBrand());
        assertEquals(brandForm.getCategory(), brandPojo.getCategory());
    }

    // Testing conversion of brand pojo to data
    @Test
    public void testConvertBrandPojoToData() {

        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setId(1);
        brandPojo.setBrand("amul");
        brandPojo.setCategory("dairy");
        BrandData brandData = DataConversionUtil.convert(brandPojo);
        assertEquals(brandPojo.getBrand(), brandData.getBrand());
        assertEquals(brandPojo.getCategory(), brandData.getCategory());
    }

    // Testing conversion of product form to pojo
    @Test
    public void testConvertProductFormToPojo() throws ApiException {

        ProductForm productForm = new ProductForm();
        productForm.setBrand("brand");
        productForm.setCategory("category0");
        productForm.setMrp(50.0);
        productForm.setName("milk");
        ProductPojo productPojo = DataConversionUtil.convert(productForm, brandPojoList.get(0));
        assertEquals(productForm.getBrand(), brandService.get(productPojo.getBrandCategory()).getBrand());
        assertEquals(productForm.getCategory(), brandService.get(productPojo.getBrandCategory()).getCategory());
        assertEquals(productForm.getName(), productPojo.getName());
        assertEquals(productForm.getMrp(), productPojo.getMrp(), 0.001);
    }

    // Testing conversion of product pojo to data
    @Test
    public void testConvertProductPojoToData() throws ApiException {

        ProductPojo productPojo = new ProductPojo();
        productPojo.setBarcode("barcode1");
        productPojo.setBrandCategory(brandPojoList.get(0).getId());
        productPojo.setMrp(50.0);
        productPojo.setName("milk");
        ProductData productData= DataConversionUtil.convert(productPojo,brandPojoList.get(0));
        assertEquals(productData.getBarcode(), productPojo.getBarcode());
        assertEquals(productData.getBrand(), brandService.get(productPojo.getBrandCategory()).getBrand());
        assertEquals(productData.getCategory(), brandService.get(productPojo.getBrandCategory()).getCategory());
        assertEquals(productData.getName(), productPojo.getName());
        assertEquals(productData.getMrp(), productPojo.getMrp(), 0.001);
    }

    // Testing conversion of inventory form to pojo
    @Test
    public void testConvertInventoryFormToPojo() throws ApiException {

        InventoryForm inventoryForm= new InventoryForm();
        inventoryForm.setBarcode(productPojoList.get(0).getBarcode());
        inventoryForm.setQuantity(20);
        InventoryPojo inventoryPojo = DataConversionUtil.convert(inventoryForm, productPojoList.get(0));
        assertEquals(inventoryForm.getQuantity(), inventoryPojo.getQuantity());
    }

    // Testing conversion of inventory pojo to data
    @Test
    public void testConvertInventoryPojoToData() throws ApiException {

        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productPojoList.get(0).getId());
        inventoryPojo.setQuantity(20);
        InventoryData inventoryData = DataConversionUtil.convert(inventoryPojo,productPojoList.get(0));
        assertEquals(inventoryData.getBarcode(), productService.get(inventoryPojo.getProductId()).getBarcode());
        assertEquals(inventoryData.getQuantity(), inventoryPojo.getQuantity());
    }

    // Testing conversion of orderItem form to pojo
    @Test
    public void testConvertOrderItemFormToPojo() throws ApiException {

        OrderItemForm orderItemForm = new OrderItemForm();
        orderItemForm.setBarcode(productPojoList.get(0).getBarcode());
        orderItemForm.setQuantity(2);
        OrderItemPojo orderItemPojo = DataConversionUtil.convert(productPojoList.get(0), orderItemForm);
        assertEquals(orderItemForm.getBarcode(), productService.get(orderItemPojo.getProductId()).getBarcode());
        assertEquals(orderItemForm.getQuantity(), orderItemPojo.getQuantity());
    }

    // Testing conversion of orderItem pojo to data
    @Test
    public void testConvertOrderItemPojoToData() throws ApiException {

        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setProductId(productPojoList.get(0).getId());
        orderItemPojo.setOrderId(orderId);
        orderItemPojo.setQuantity(2);
        orderItemPojo.setSp(30.0);
        OrderItemData data = DataConversionUtil.convert(orderItemPojo,productPojoList.get(0));
        assertEquals(data.getBarcode(), productService.get(orderItemPojo.getProductId()).getBarcode());
        assertEquals(data.getOrderId(), orderItemPojo.getOrderId());
        assertEquals(data.getQuantity(), orderItemPojo.getQuantity());
    }

    // Testing conversion of list of brand pojo to data
    @Test
    public void testListBrandPojoToData() {
        List<BrandPojo> brandPojoList = brandService.getAll();
        List<BrandData> brandDataList = DataConversionUtil.convert(brandPojoList);
        assertEquals(brandPojoList.size(), brandDataList.size());
        assertEquals(brandPojoList.get(0).getBrand(), brandDataList.get(0).getBrand());
        assertEquals(brandPojoList.get(0).getCategory(), brandDataList.get(0).getCategory());
    }


    // Testing conversion of list of order items to invoice
    @Test
    public void testOrderItemstoInvoice() throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderService.getAll();
        Map<OrderItemPojo,ProductPojo> productPojoList=orderService.getProductPojos(this.orderItemPojoList);
        OrderInvoiceXmlList orderInvoiceXmlList = DataConversionUtil.convertToInvoiceDataList(orderItemPojoList,productPojoList);
        assertEquals(orderItemPojoList.size(), orderInvoiceXmlList.getOrderInvoiceData().size());
        assertEquals(orderInvoiceXmlList.getOrderInvoiceData().get(0).getName(), productService.get(orderItemPojoList.get(0).getProductId()).getName());
        assertEquals(orderInvoiceXmlList.getOrderInvoiceData().get(0).getQuantity(), orderItemPojoList.get(0).getQuantity());
        assertEquals(orderInvoiceXmlList.getOrderInvoiceData().get(0).getMrp(), orderItemPojoList.get(0).getSp(), 0.001);
    }


    // Testing conversion of list of orderItem forms to pojo
    @Test
    public void testOrderItemsFormtoPojo() throws ApiException {
        OrderItemForm[] orderItemForms = new OrderItemForm[1];
        OrderItemForm orderItemForm = new OrderItemForm();
        orderItemForm.setBarcode(productPojoList.get(0).getBarcode());
        orderItemForm.setQuantity(2);
        orderItemForms[0] = orderItemForm;
        Map<String, ProductPojo> barcode_product = new HashMap<>();
        barcode_product.put(productPojoList.get(0).getBarcode(), productPojoList.get(0));
        barcode_product.put(productPojoList.get(1).getBarcode(), productPojoList.get(1));

        List<OrderItemPojo> orderItemPojoList = DataConversionUtil.convertOrderItemForms(barcode_product, orderItemForms);
        assertEquals(1, orderItemPojoList.size());
        assertEquals(orderItemForms[0].getBarcode(), productService.get(orderItemPojoList.get(0).getProductId()).getBarcode());
        assertEquals(orderItemForms[0].getQuantity(), orderItemPojoList.get(0).getQuantity());
    }

    // Testing conversion of order pojo to data
    @Test
    public void testConvertOrderPojoToData() throws ApiException {
        OrderPojo orderPojo = orderService.getOrder(orderId);
        OrderData orderData = DataConversionUtil.convert(orderPojo);
        assertEquals(orderPojo.getId(), orderData.getId());
    }

    // Test conversion to inventory report list
    @Test
    public void testConvertInventoryReportList() {
        Map<BrandPojo, Integer> quantityPerBrandPojo = new HashMap<>();
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand("brand1");
        brandPojo.setCategory("category1");
        BrandPojo brandPojo1 = new BrandPojo();
        brandPojo1.setBrand("brand2");
        brandPojo1.setCategory("category2");
        quantityPerBrandPojo.put(brandPojo, 1);
        quantityPerBrandPojo.put(brandPojo1, 2);
        InventoryXmlList inv_list = DataConversionUtil.convertInventoryReportList(quantityPerBrandPojo);
        assertEquals(2, inv_list.getInventoryReportData().size());
    }

    // Test conversion to sales list
    @Test
    public void testConvertSalesList() {
        Map<BrandPojo, Integer> quantityPerBrandPojo = new HashMap<>();
        Map<BrandPojo, Double> revenuePerBrandCategory = new HashMap<>();
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand("brand1");
        brandPojo.setCategory("category1");
        BrandPojo brandPojo1 = new BrandPojo();
        brandPojo1.setBrand("brand2");
        brandPojo1.setCategory("category2");
        quantityPerBrandPojo.put(brandPojo, 1);
        quantityPerBrandPojo.put(brandPojo1, 2);
        revenuePerBrandCategory.put(brandPojo, 100.00);
        revenuePerBrandCategory.put(brandPojo1, 200.00);
        SaleXmlList saleXmlList = DataConversionUtil.convertSalesList(quantityPerBrandPojo, revenuePerBrandCategory);
        assertEquals(2, saleXmlList.getSaleReportDataList().size());
    }
}
