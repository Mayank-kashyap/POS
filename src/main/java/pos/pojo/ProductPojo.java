package pos.pojo;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Data
@Entity
@Proxy(lazy=false)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"barcode"})})
public class ProductPojo {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String barcode;
    private String name;
    private Double mrp;

    //Many to one mapping with brand pojo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="brand_category", referencedColumnName="id"),
    })
    private BrandPojo brandPojo;
}
