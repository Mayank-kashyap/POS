package pos.pojo;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Data
@Entity
@Proxy(lazy = false)
public class OrderItemPojo {

    //Generate id starting from 100000
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "orderItemIdSequence")
    @SequenceGenerator(name = "orderItemIdSequence",initialValue = 100000, allocationSize = 1, sequenceName = "orderItemId")
    private Integer id;
    private Integer quantity;
    private Double sp;

    //Many to one mapping with order pojo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="orderId", referencedColumnName="id"),
    })
    private OrderPojo orderPojo;

    //Many to one mapping with product pojo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="productId", referencedColumnName="id"),
    })
    private ProductPojo product;

    public BrandPojo getBrand(){
        return product.getBrandPojo();
    }
    public double getRevenue() {
        return quantity*sp;
    }

}
