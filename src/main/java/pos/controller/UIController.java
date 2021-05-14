package pos.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
class UIController extends AbstractUiController{

    @Value("${app.baseUrl}")
    private String baseUrl;

    @RequestMapping(value = "")
    public ModelAndView index() {
        return mav("home.html");
    }

    @RequestMapping(value = "/site/brand")
    public ModelAndView features() {
        return mav("brand.html");
    }

    @RequestMapping(value = "/site/product")
    public ModelAndView pricing() {
        return mav("product.html");
    }

    @RequestMapping(value = "/site/inventory")
    public ModelAndView inventory() {
        return mav("inventory.html");
    }

    @RequestMapping(value = "/site/sales_report")
    public ModelAndView sales_report() {
        return mav("sales_report.html");
    }

    @RequestMapping(value = "/site/order")
    public ModelAndView ordering() {
        return mav("order.html");
    }

}
