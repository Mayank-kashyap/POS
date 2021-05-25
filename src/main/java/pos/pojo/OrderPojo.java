package pos.pojo;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class OrderPojo {

    //Generate id from 1000
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "orderIdSequence")
    @SequenceGenerator(name = "orderIdSequence",initialValue = 1000, allocationSize = 1, sequenceName = "orderId")
    private Integer id;
    private LocalDateTime datetime;
    private Boolean isInvoiceGenerated;
}
