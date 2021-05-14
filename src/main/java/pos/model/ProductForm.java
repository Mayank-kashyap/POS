package pos.model;

import lombok.Data;

@Data
public class ProductForm {

    private String barcode;
    private String name;
    private Double mrp;
    private String brand;
    private String category;
}
