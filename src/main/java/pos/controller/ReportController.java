package pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pos.model.ReportFilter;
import pos.service.ReportService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Controls all the report generation
@Api
@RestController
public class ReportController extends ExceptionHandler{

    @Autowired
    private ReportService reportService;

    //Retrieves the brand report
    @ApiOperation(value = "Gets Brand Report")
    @RequestMapping(path = "/api/report/brand", method = RequestMethod.GET)
    public void get(HttpServletResponse response) throws Exception {
        byte[] bytes = reportService.generatePdfResponse("brand");
        createPdfResponse(bytes, response);
    }

    //Retrieves the inventory report
    @ApiOperation(value = "Gets Inventory Report")
    @RequestMapping(path = "/api/report/inventory", method = RequestMethod.GET)
    public void getInventory(HttpServletResponse response) throws Exception {
        byte[] bytes = reportService.generatePdfResponse("inventory");
        createPdfResponse(bytes, response);
    }

    //Retrieves the sales report
    @ApiOperation(value = "Gets Sales Report")
    @RequestMapping(path = "/api/report/sales", method = RequestMethod.POST)
    public void getSales(@RequestBody ReportFilter reportFilter, HttpServletResponse response) throws Exception {
        byte[] bytes = reportService.generatePdfResponse("sales", reportFilter);
        createPdfResponse(bytes, response);
    }

    public void createPdfResponse(byte[] bytes, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setContentLength(bytes.length);

        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }
}