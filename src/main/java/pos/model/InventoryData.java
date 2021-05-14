package pos.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryData extends InventoryForm{
    private Integer id;
}
