package org.openmrs.module.webservices.rest.web.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.docs.SwaggerSpecificationCreator;
import org.openmrs.module.webservices.rest.web.RestConstants;
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
			
			String resourcesUrl = Context.getAdministrationService().getGlobalProperty(
			    RestConstants.URI_PREFIX_GLOBAL_PROPERTY_NAME, baseUrl.toString());
			
			resourcesUrl += "/ws";
			
			baseUrl.append("/moduleResources/webservices/rest/test.json");
			
			URL swaggerSpec = new URL(baseUrl.toString());
			BufferedReader in = new BufferedReader(new InputStreamReader(swaggerSpec.openStream()));
			
			SwaggerSpecificationCreator creator = new SwaggerSpecificationCreator(resourcesUrl);
			
			String fileSeparator = System.getProperty("file.separator");
			
			System.out.println(" URL " + baseUrl.toString());
			File file = new File("C:" + fileSeparator + "Users" + fileSeparator + "zakaria" + fileSeparator + "Desktop"
			        + fileSeparator + "output.json");
			
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(creator.BuildJSON());
			bw.close();
			
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
