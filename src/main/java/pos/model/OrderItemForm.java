package pos.model;

import lombok.Data;

@Data
public class OrderItemForm {
    private String barcode;
    private Integer quantity;
    private Double sp;
}
