package pos.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pos.util.IOUtil;

//Controls the generation of sample tsv files
@Controller
public class SampleController extends ExceptionHandler{

    @RequestMapping(value = "/sample/{fileName:.+}", method = RequestMethod.GET)
    public void getFile(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        // get your file as InputStream
        response.setContentType("text/csv");
        response.addHeader("Content-disposition:", "attachment; filename=" + fileName);
        String fileClasspath = "/pos/" + fileName;
        System.out.println(fileClasspath);
        InputStream is = SampleController.class.getResourceAsStream(fileClasspath);
        // copy it to response's OutputStream
        try {
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } finally {
            IOUtil.closeQuietly(is);
        }
    }

}
