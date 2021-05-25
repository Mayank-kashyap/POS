package pos.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "brands")
@XmlAccessorType(XmlAccessType.FIELD)
public class BrandXmlList {

    @XmlElement(name = "brand_item")
    List<BrandData> brandDataList;
}
