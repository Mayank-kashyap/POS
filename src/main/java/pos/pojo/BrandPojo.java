package pos.pojo;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Data
@Entity
@Proxy(lazy=false)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"brand","category"})})
public class BrandPojo {

    //Generate id from 1
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String brand;
    private String category;
}
