package pos.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class InventoryReportData {
    @XmlElement
    private String brand;
    @XmlElement
    private String category;
    @XmlElement
    private Integer quantity;
}
