package pos.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderItemData extends OrderItemForm{
    private Integer id;
    private Integer orderId;
}
