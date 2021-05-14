package pos.pojo;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Data
@Entity
@Proxy(lazy=false)
public class InventoryPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Many to one mapping with product pojo
    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.ALL})
    @JoinColumns({
            @JoinColumn(name="product_id", referencedColumnName="id"),
    })
    private ProductPojo product;
    private Integer quantity;

    public BrandPojo getBrandPojo(){
        return product.getBrandPojo();
    }
}
