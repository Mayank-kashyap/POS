package pos.model;

import lombok.Data;

@Data
public class ReportFilter {
    //todo change to date type
    private String startDate;
    private String endDate;
    private String brand;
    private String category;
}
