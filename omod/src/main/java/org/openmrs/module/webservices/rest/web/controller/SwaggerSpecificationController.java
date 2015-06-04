package org.openmrs.module.webservices.rest.web.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("SwaggerSpecificationController")
@RequestMapping("/module/webservices/rest/swaggerSpec.json")
public class SwaggerSpecificationController {
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	JSONObject getSwaggerSpecification(HttpServletRequest request) throws IllegalAccessException, InstantiationException,
	        IOException, ConversionException {
		JSONParser parser = new JSONParser();
		JSONObject object = new JSONObject();
		try {
			StringBuilder baseUrl = new StringBuilder();
			String scheme = request.getScheme();
			int port = request.getServerPort();
			
			baseUrl.append(scheme); // http, https
			baseUrl.append("://");
			baseUrl.append(request.getServerName());
			if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
				baseUrl.append(':');
				baseUrl.append(request.getServerPort());
			}
			
			baseUrl.append(request.getContextPath());
			baseUrl.append("/moduleResources/webservices/rest/test.json");
			
			URL swaggerSpec = new URL(baseUrl.toString());
			BufferedReader in = new BufferedReader(new InputStreamReader(swaggerSpec.openStream()));
			
			System.out.println(" URL " + baseUrl.toString());
			
			Object temp = parser.parse(in);
			object = (JSONObject) temp;
		}
		catch (Exception exception) {
			System.out.println(" Exception Reading from file ---------------");
			exception.printStackTrace();
		}
		return object;
	}
	
}
