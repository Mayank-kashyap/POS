package pos.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

//Data about brand
@EqualsAndHashCode(callSuper = true)
@Data
public class BrandData extends BrandForm{

    private Integer id;
}
