package org.openmrs.module.webservices.docs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

public class SwaggerSpecificationCreator {
	
	public static final String JSON_PATH = "";
	
	private JSONObject swaggerSpecification;
	
	private String baseUrl;
	
	public SwaggerSpecificationCreator(String baseUrl) {
		swaggerSpecification = new JSONObject();
		this.baseUrl = baseUrl;
	}
	
	public String BuildJSON() {
		synchronized (this) {
			AddApiDefinition();
			//AddPaths();
		}
		
		return swaggerSpecification.toString();
	}
	
	private void AddApiDefinition() {
		swaggerSpecification.put("swagger", "2.0");
		JSONObject info = new JSONObject();
		info.put("version", "");
		info.put("title", "OpenMRS Rest Services");
		info.put("description", "");
		info.put("termsOfService", "");
		info.put("contact", new JSONObject().put("name", "OpenMRS"));
		info.put("license", new JSONObject().put("name", "MIT"));
		swaggerSpecification.put("info", info);
		swaggerSpecification.put("host", baseUrl);
		swaggerSpecification.put("basePath", "ws");
		JSONArray schemes = new JSONArray();
		schemes.add("http");
		swaggerSpecification.put("schemes", schemes);
		JSONArray consumes = new JSONArray();
		consumes.add("application/json");
		swaggerSpecification.put("consumes", consumes);
		JSONArray produces = new JSONArray();
		produces.add("application/json");
		swaggerSpecification.put("produces", produces);
	}
	
	private void AddPaths() {
		
		List<DelegatingResourceHandler<?>> resourceHandlers = Context.getService(RestService.class).getResourceHandlers();
		
		Collections.sort(resourceHandlers, new Comparator<DelegatingResourceHandler<?>>() {
			
			@Override
			public int compare(DelegatingResourceHandler<?> left, DelegatingResourceHandler<?> right) {
				return isSubclass(left).compareTo(isSubclass(right));
			}
			
			private Boolean isSubclass(DelegatingResourceHandler<?> resourceHandler) {
				return resourceHandler.getClass().getAnnotation(
				    org.openmrs.module.webservices.rest.web.annotation.SubResource.class) != null;
			}
		});
		
		for (DelegatingResourceHandler<?> resourceHandler : resourceHandlers) {
			if (resourceHandler.getClass().getName()
			        .equals("org.openmrs.module.webservices.rest.web.HivDrugOrderSubclassHandler")
			        || resourceHandler.getClass().getName()
			                .equals("org.openmrs.module.webservices.rest.web.v1_0.test.GenericChildResource")) {
				continue; //Skip the test class
			}
			
			Object delegate = null;
			try {
				delegate = resourceHandler.newDelegate();
			}
			catch (ResourceDoesNotSupportOperationException ex) {
				continue;
			}
			if (delegate == null) {
				// TODO: handle resources that don't implement newDelegate(), e.g. ConceptSearchResource1_9, all subclasses of EvaluatedResource in the reportingrest module
				continue;
			}
			
			String resourceClassname = delegate.getClass().getSimpleName();
			if (resourceClassname.equals("UserAndPassword1_8")) {
				resourceClassname = "User"; //Work-around for UserAndPassword to be displayed as User
			} else if (resourceClassname.equals("CohortMember1_8")) {
				resourceClassname = "CohortMember";
			} else if (resourceClassname.equals("IncomingHl7Message1_8")) {
				resourceClassname = "HL7";
			}
			
			String subResourceForClass = null;
			ResourceDoc resourceDoc = new ResourceDoc(resourceClassname);
			resourceDoc.setResourceVersion(resourceHandler.getResourceVersion());
			org.openmrs.module.webservices.rest.web.annotation.Resource resourceAnnotation = ((org.openmrs.module.webservices.rest.web.annotation.Resource) resourceHandler
			        .getClass().getAnnotation(org.openmrs.module.webservices.rest.web.annotation.Resource.class));
			if (resourceAnnotation != null) {
				resourceDoc.setResourceName(resourceAnnotation.name());
				
				String[] supportedVersions = resourceAnnotation.supportedOpenmrsVersions();
				List<String> supportedVersionsList = new ArrayList<String>();
				
				for (String version : supportedVersions)
					supportedVersionsList.add(version);
				
				resourceDoc.setSupportedOpenMRSVersion(supportedVersionsList);
				
				addResourceGetOperation(resourceDoc);
				addResourceGetOperationWithId(resourceDoc);
				
			} else {
				//this is a subResource, use the name of the collection
				org.openmrs.module.webservices.rest.web.annotation.SubResource subResourceAnnotation = ((org.openmrs.module.webservices.rest.web.annotation.SubResource) resourceHandler
				        .getClass().getAnnotation(org.openmrs.module.webservices.rest.web.annotation.SubResource.class));
				if (subResourceAnnotation != null) {
					org.openmrs.module.webservices.rest.web.annotation.Resource parentResourceAnnotation = ((org.openmrs.module.webservices.rest.web.annotation.Resource) subResourceAnnotation
					        .parent().getAnnotation(org.openmrs.module.webservices.rest.web.annotation.Resource.class));
					
					resourceDoc.setResourceName(parentResourceAnnotation.name());
					resourceDoc.setSubResourceName(subResourceAnnotation.path());
					
					subResourceForClass = parentResourceAnnotation.supportedClass().getSimpleName();
				}
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addResourceGetOperation(ResourceDoc resource) {
		JSONObject getRoot = new JSONObject();
		
		JSONObject get = new JSONObject();
		get.put("description", "");
		get.put("operationId", "find" + resource.getName());
		JSONArray consumes = new JSONArray();
		consumes.add("application/json");
		get.put("produces", consumes);
		
		JSONArray parameters = getParametersFromResourceRepresentations(resource.getRepresentations(), "GET",
		    resource.getName());
		
		get.put("parameters", parameters);
		
		JSONObject responses = new JSONObject();
		JSONObject statusOK = new JSONObject();
		statusOK.put("description", "");
		JSONObject schema = new JSONObject();
		schema.put("$ref", "#/definitions/" + resource.getName());
		statusOK.put("schema", schema);
		responses.put("200", statusOK);
		get.put("responses", responses);
		
		getRoot.put("get", get);
		swaggerSpecification.put("/" + resource.getName(), getRoot);
		
	}
	
	@SuppressWarnings("unchecked")
	private void addResourceGetOperationWithId(ResourceDoc resource) {
		
		JSONObject operationRoot = new JSONObject();
		
		JSONObject operation = new JSONObject();
		operation.put("description", "");
		operation.put("operationId", "find" + resource.getName());
		JSONArray produces = new JSONArray();
		produces.add("application/json");
		operation.put("produces", produces);
		JSONArray parameters = new JSONArray();
		
		JSONObject idParameter = new JSONObject();
		idParameter.put("name", "uuid");
		idParameter.put("in", "path");
		idParameter.put("description", "uuid of resource to fetch");
		idParameter.put("required", "true");
		idParameter.put("type", "integer");
		idParameter.put("format", "int64");
		
		parameters.add(idParameter);
		operation.put("parameters", parameters);
		
		JSONObject responses = new JSONObject();
		JSONObject statusOK = new JSONObject();
		statusOK.put("description", "");
		JSONObject schema = new JSONObject();
		schema.put("$ref", "#/definitions/" + resource.getName());
		statusOK.put("schema", schema);
		responses.put("200", statusOK);
		operation.put("responses", responses);
		
		operationRoot.put("get", operation);
		swaggerSpecification.put("/" + resource.getName() + "/{uuid}", operationRoot);
		
	}
	
	private void addSubResource(ResourceDoc resource) {
		
	}
	
	private JSONArray getParametersFromResourceRepresentations(List<ResourceRepresentation> representations,
	        String operation, String resourceName) {
		
		JSONArray parameters = new JSONArray();
		for (int i = 0; i < representations.size(); i++) {
			String tempRepresentationName = representations.get(i).getName();
			String tempOperation = (tempRepresentationName.split(" "))[0];
			if (operation.equals(tempOperation)) {
				JSONObject parameter = new JSONObject();
				parameter.put("name", resourceName);
				parameter.put("in", "body");
				parameter.put("description", "");
				parameter.put("required", "false");
				if (operation.equals("GET")) {
					JSONObject schema = new JSONObject();
					schema.put("$ref", "#/definitions/" + resourceName);
					parameter.put("schema", schema);
				} else {
					JSONObject schema = new JSONObject();
					schema.put("$ref", "#/definitions/" + resourceName + "Input");
					parameter.put("schema", schema);
				}
				parameters.add(parameter);
				break;
			}
		}
		return parameters;
	}
}
