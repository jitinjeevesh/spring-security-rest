package com.oauth.request;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CSRFRequest {

    @RequestMapping(value = "/csrf", method = RequestMethod.GET)
    public ResponseEntity<Map> csrf() {
        Map<String, String> responseMap = new HashMap<String, String>();
        responseMap.put("message", "CRF token generate successfully");
        return new ResponseEntity<Map>(responseMap, HttpStatus.OK);
    }
}
