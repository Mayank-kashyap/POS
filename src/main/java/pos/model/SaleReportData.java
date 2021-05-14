package pos.model;

import lombok.Data;

@Data
public class SaleReportData {
    private String brand;
    private String category;
    private Integer quantity;
    private Double revenue;
}
