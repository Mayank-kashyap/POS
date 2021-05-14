package pos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pos.model.AboutAppData;
import pos.service.AboutAppService;

@Api
@RestController
public class AppController {

    @Autowired
    private AboutAppService service;

    //Controls the about of the application
    @ApiOperation(value="Gives application name and version")
    @RequestMapping(path = "/api/about", method = RequestMethod.GET)
    public AboutAppData getDetails(){
        AboutAppData aboutAppData = new AboutAppData();
        aboutAppData.setName(service.getName());
        aboutAppData.setVersion(service.getVersion());
        return aboutAppData;
    }
}
