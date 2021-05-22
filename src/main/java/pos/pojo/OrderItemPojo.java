package pos.pojo;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Data
@Entity
public class OrderItemPojo {

    //Generate id starting from 100000
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "orderItemIdSequence")
    @SequenceGenerator(name = "orderItemIdSequence",initialValue = 100000, allocationSize = 1, sequenceName = "orderItemId")
    private Integer id;
    private Integer quantity;
    private Double sp;
    private Integer orderId;
    private Integer ProductId;

    public double getRevenue() {
        return quantity*sp;
    }

}
