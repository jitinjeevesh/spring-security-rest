package sample;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiController {

    Logger log = Logger.getLogger(this.getClass().getName());

    @RequestMapping(value = "/api/test", method = RequestMethod.POST)
    public ResponseEntity<Map> test(@RequestBody RequestCommand pushCommand, HttpServletRequest httpServletRequest) {
        log.info("************* Recieved request for test api ****************");
        Map<String, String> responseMap = new HashMap<String, String>();
        String message = "";
        try {
            message = pushCommand.getMessage();
            log.info("************** recieved Message : " + message);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        responseMap.put("message", message);
        responseMap.put("status", "1");
        responseMap.put("code", "200");
        log.info("************ Sending Response ***************");
        return new ResponseEntity<Map>(responseMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/save", method = RequestMethod.POST)
    public String save() {
        return "Hello";
    }

    @RequestMapping(value = "/api1/save", method = RequestMethod.POST)
    public String save1() {
        return "Hello";
    }

}
