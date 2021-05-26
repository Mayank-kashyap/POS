package pos.model;

import lombok.Data;

@Data
public class OrderData {
    private Integer id;
    private String datetime;
    private Boolean isInvoiceGenerated;
}
