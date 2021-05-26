package pos.model;

import lombok.Data;

@Data
public class ReportFilter {
    private String startDate;
    private String endDate;
    private String brand;
    private String category;
}
