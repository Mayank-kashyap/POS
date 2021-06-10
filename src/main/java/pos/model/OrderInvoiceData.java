package pos.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderInvoiceData {
    @XmlElement
    private String name;
    @XmlElement
    private Double mrp;
    @XmlElement
    private Integer quantity;
}
