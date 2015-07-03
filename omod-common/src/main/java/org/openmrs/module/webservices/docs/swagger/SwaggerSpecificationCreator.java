/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.docs.swagger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.docs.ResourceDoc;
import org.openmrs.module.webservices.docs.ResourceOperation;
import org.openmrs.module.webservices.docs.ResourceRepresentation;
import org.openmrs.module.webservices.docs.SearchHandlerDoc;
import org.openmrs.module.webservices.docs.SearchQueryDoc;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription.Property;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainSubResourceController;

public class SwaggerSpecificationCreator {
	
	private SwaggerSpecification swaggerSpecification;
	
	private String baseUrl;
	
	private static List<ResourceDoc> resourceDocList = new ArrayList<ResourceDoc>();
	
	private static List<SearchHandlerDoc> searchHandlerDocs;
	
	public SwaggerSpecificationCreator(String baseUrl) {
		this.swaggerSpecification = new SwaggerSpecification();
		this.baseUrl = baseUrl;
		List<SearchHandler> searchHandlers = Context.getService(RestService.class).getAllSearchHandlers();
		searchHandlerDocs = fillSearchHandlers(searchHandlers, baseUrl);
	}
	
	public String BuildJSON() {
		synchronized (this) {
			CreateApiDefinition();
			AddPaths();
			CreateObjectDefintions();
			AddResourceTags();
		}
		
		//serialize();
		return CreateJSON();
	}
	
	private void CreateApiDefinition() {
		
		Info info = new Info();
		info.setVersion("1.0.0");
		info.setTitle("OpenMRS Rest Services");
		info.setDescription("auto-generated documentation for OpenMRS Rest services");
		Contact contact = new Contact();
		contact.setName("OpenMRS Rest Module Team");
		License license = new License();
		license.setName("MIT");
		info.setContact(contact);
		info.setLicense(license);
		swaggerSpecification.setInfo(info);
		List<String> produces = new ArrayList<String>();
		produces.add("application/json");
		List<String> consumes = new ArrayList<String>();
		consumes.add("application/json");
		swaggerSpecification.setHost(baseUrl);
		swaggerSpecification.setBasePath("/" + RestConstants.VERSION_1);
		swaggerSpecification.setProduces(produces);
		swaggerSpecification.setConsumes(consumes);
	}
	
	private void AddPaths() {
		
		List<DelegatingResourceHandler<?>> resourceHandlers = Context.getService(RestService.class).getResourceHandlers();
		Paths paths = new Paths();
		
		Map<String, Path> pathMap = new HashMap<String, Path>();
		
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
			
			Object delegate = null;
			try {
				delegate = resourceHandler.newDelegate();
			}
			catch (ResourceDoesNotSupportOperationException ex) {
				continue;
			}
			if (delegate == null) {
				// TODO: handle resources that don't implement newDelegate(), e.g. ConceptSearchResource1_9, all subclasses of EvaluatedResource in the reporting rest module
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
				
				for (String version : supportedVersions) {
					supportedVersionsList.add(version);
				}
				
				resourceDoc.setSupportedOpenMRSVersion(supportedVersionsList);
				
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
			
			Object instance = resourceHandler;
			
			//GET representations
			Representation[] representations = new Representation[] { Representation.REF, Representation.DEFAULT,
			        Representation.FULL };
			
			for (Representation representation : representations) {
				if (instance instanceof Converter) {
					try {
						@SuppressWarnings("unchecked")
						Converter<Object> converter = (Converter<Object>) instance;
						SimpleObject simpleObject = converter.asRepresentation(delegate, representation);
						resourceDoc.addRepresentation(new ResourceRepresentation(
						        "GET " + representation.getRepresentation(), simpleObject.keySet()));
					}
					catch (Exception e) {
						resourceDoc.addRepresentation(new ResourceRepresentation(
						        "GET " + representation.getRepresentation(), Arrays.asList("Not supported")));
					}
				} else {
					resourceDoc.addRepresentation(new ResourceRepresentation("GET " + representation.getRepresentation(),
					        Arrays.asList("Not supported")));
				}
			}
			
			//POST create representations
			try {
				DelegatingResourceDescription description = resourceHandler.getCreatableProperties();
				List<String> properties = getPOSTProperties(description);
				resourceDoc.addRepresentation(new ResourceRepresentation("POST create", properties));
			}
			catch (ResourceDoesNotSupportOperationException e) {
				resourceDoc.addRepresentation(new ResourceRepresentation("POST create", Arrays.asList("Not supported")));
			}
			
			//POST update representations
			try {
				DelegatingResourceDescription description = resourceHandler.getUpdatableProperties();
				List<String> properties = getPOSTProperties(description);
				resourceDoc.addRepresentation(new ResourceRepresentation("POST update", properties));
			}
			catch (ResourceDoesNotSupportOperationException e) {
				resourceDoc.addRepresentation(new ResourceRepresentation("POST update", Arrays.asList("Not supported")));
			}
			
			Path path = new Path();
			Path path2 = new Path();
			
			Map<String, Operation> operationsMap = new HashMap<String, Operation>();
			Map<String, Operation> operationsWithUUIDMap = new HashMap<String, Operation>();
			
			for (ResourceRepresentation representation : resourceDoc.getRepresentations()) {
				String resourceLongName = resourceDoc.getResourceName();
				String resourceURL = resourceDoc.getUrl();
				if (resourceLongName != null) {
					String tempRepresentationName = representation.getName();
					String tempOperation = (tempRepresentationName.split(" "))[0];
					String operationType = (tempRepresentationName.split(" "))[1];
					
					String resourceName = (resourceLongName.split("/"))[1];
					
					//For Get Representation
					if (tempOperation.equals("GET")) {
						if (operationType.equals("full")) {
							//Get resource
							Operation operationGet = CreateOperation("get", resourceName, representation, OperationEnum.get);
							operationsMap.put("get", operationGet);
							path.setOperations(operationsMap);
							pathMap.put("/" + resourceName, path);
							
							//Get resource/{uuid} 
							Operation operationGetWithUUID = CreateOperation("get", resourceName, representation,
							    OperationEnum.getWithUUID);
							operationsWithUUIDMap.put("get", operationGetWithUUID);
							path2.setOperations(operationsWithUUIDMap);
							pathMap.put("/" + resourceName + "/{uuid}", path2);
						}
					}// For Post Representation
					else {
						//Post create
						if (operationType.equals("create")) {
							Operation operationPostCreate = CreateOperation("post", resourceName, representation,
							    OperationEnum.postCreate);
							operationsMap.put("post", operationPostCreate);
							path.setOperations(operationsMap);
							
							pathMap.put("/" + resourceName, path);
						} else {
							//Post update
							Operation operationPostUpdate = CreateOperation("post", resourceName, representation,
							    OperationEnum.postUpdate);
							operationsWithUUIDMap.put("post", operationPostUpdate);
							path2.setOperations(operationsWithUUIDMap);
							pathMap.put("/" + resourceName + "/{uuid}", path2);
						}
					}
				}
				resourceDocList.add(resourceDoc);
			}
			
		}
		paths.setPaths(pathMap);
		swaggerSpecification.setPaths(paths);
	}
	
	private void CreateObjectDefintions() {
		Definitions definitions = new Definitions();
		Map<String, Definition> definitionsMap = new HashMap<String, Definition>();
		
		for (ResourceDoc doc : resourceDocList) {
			String resourceLongName = doc.getResourceName();
			if (resourceLongName != null) {
				Definition definition = new Definition();
				definition.setType("object");
				Properties properties = new Properties();
				Map<String, DefinitionProperty> propertiesMap = new HashMap<String, DefinitionProperty>();
				String resourceName = (resourceLongName.split("/"))[1];
				String resourceDefinitionName = resourceName;
				for (ResourceRepresentation representation : doc.getRepresentations()) {
					String tempRepresentationName = representation.getName();
					String tempOperation = (tempRepresentationName.split(" "))[0];
					String operationType = (tempRepresentationName.split(" "))[1];
					for (String representationProperty : representation.getProperties()) {
						DefinitionProperty property = new DefinitionProperty();
						//all properties are of type string
						property.setType("string");
						String propertyNameWithoutStar = "";
						if (representationProperty.startsWith("*"))
							propertyNameWithoutStar = representationProperty.replace("*", "");
						else
							propertyNameWithoutStar = representationProperty;
						
						propertiesMap.put(propertyNameWithoutStar, property);
					}
					
					//Definitions for POST CREATE and POST UPDATE
					if (!tempOperation.equals("GET")) {
						if (operationType.equals("create"))
							resourceDefinitionName = resourceName + "CreateInput";
						else
							resourceDefinitionName = resourceName + "UpdateInput";
						
					}
					properties.setProperties(propertiesMap);
					definition.setProperties(properties);
					definitionsMap.put(resourceDefinitionName, definition);
					
				}
				
			}
		}
		
		definitions.setDefinitions(definitionsMap);
		swaggerSpecification.setDefinitions(definitions);
	}
	
	/**
	 * @return the swaggerSpecification
	 */
	public SwaggerSpecification getSwaggerSpecification() {
		return swaggerSpecification;
	}
	
	private static List<String> getPOSTProperties(DelegatingResourceDescription description) {
		List<String> properties = new ArrayList<String>();
		for (Entry<String, Property> property : description.getProperties().entrySet()) {
			if (property.getValue().isRequired()) {
				properties.add("*" + property.getKey() + "*");
			} else {
				properties.add(property.getKey());
			}
		}
		return properties;
	}
	
	private List<Parameter> getParametersList(Collection<String> properties, String resourceName, OperationEnum operationEnum) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		String resourceURL = getResourceUrl(baseUrl, resourceName);
		if (operationEnum == OperationEnum.get) {
			for (SearchHandlerDoc searchDoc : searchHandlerDocs) {
				System.out.println(" 1 " + resourceURL + " 2 " + searchDoc.getResourceURL());
				if (searchDoc.getResourceURL().equals(resourceURL)) {
					System.out.println("Inside");
					for (SearchQueryDoc queryDoc : searchDoc.getSearchQueriesDoc()) {
						for (String requiredParameter : queryDoc.getRequiredParameters()) {
							Parameter parameter = new Parameter();
							parameter.setName(requiredParameter);
							parameter.setIn("query");
							parameter.setDescription(queryDoc.getDescription());
							parameter.setRequired(true);
							parameters.add(parameter);
						}
						for (String optionalParameter : queryDoc.getOptionalParameters()) {
							Parameter parameter = new Parameter();
							parameter.setName(optionalParameter);
							parameter.setIn("query");
							parameter.setDescription(queryDoc.getDescription());
							parameter.setRequired(false);
							parameters.add(parameter);
						}
					}
				}
			}
			
			/*for (String property : properties) {
				Parameter parameter = new Parameter();
				parameter.setName(property);
				parameter.setIn("query");
				parameter.setDescription(property + " to filter by");
				parameter.setRequired(false);
				parameters.add(parameter);
			}*/
		} else if (operationEnum == OperationEnum.getWithUUID) {
			Parameter parameter = new Parameter();
			parameter.setName("uuid");
			parameter.setIn("path");
			parameter.setDescription("uuid to filter by");
			parameter.setRequired(true);
			parameters.add(parameter);
		} else if (operationEnum == OperationEnum.postCreate) {
			for (String property : properties) {
				Parameter parameter = new Parameter();
				parameter.setName(property);
				parameter.setIn("query");
				parameter.setDescription(property + " of the new entity");
				
				if (property.startsWith("*")) {
					parameter.setRequired(true);
					String propertyStringWithoutStar = property.replace("*", "");
					parameter.setName(propertyStringWithoutStar);
				} else {
					parameter.setRequired(false);
				}
				
				parameters.add(parameter);
			}
			
		} else if (operationEnum == OperationEnum.postUpdate) {
			for (String property : properties) {
				Parameter parameter = new Parameter();
				parameter.setName(property);
				parameter.setIn("query");
				parameter.setDescription(property + " to be updated");
				
				if (property.startsWith("*")) {
					parameter.setRequired(true);
					String propertyStringWithoutStar = property.replace("*", "");
					parameter.setName(propertyStringWithoutStar);
				} else {
					parameter.setRequired(false);
				}
				parameters.add(parameter);
			}
			
			Parameter parameter = new Parameter();
			parameter.setName("uuid");
			parameter.setIn("path");
			parameter.setDescription("uuid of the resource to update");
			parameter.setRequired(true);
			parameters.add(parameter);
			
		}
		
		return parameters;
	}
	
	private String CreateJSON() {
		String json = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.setSerializationInclusion(Include.NON_NULL);
			mapper.getSerializerProvider().setNullKeySerializer(new NullSerializer());
			
			json = mapper.writeValueAsString(swaggerSpecification);
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
		
		return json;
	}
	
	private void AddResourceTags() {
		
		List<Tag> tags = new ArrayList<Tag>();
		for (ResourceDoc doc : resourceDocList) {
			String resourceLongName = doc.getResourceName();
			if (resourceLongName != null) {
				String resourceName = (resourceLongName.split("/"))[1];
				Tag tag = new Tag();
				tag.setName(resourceName);
				/* For now, we do not add any description */
				tag.setDescription("");
				tags.add(tag);
			}
		}
		
		swaggerSpecification.setTags(tags);
	}
	
	private Operation CreateOperation(String operationName, String resourceName, ResourceRepresentation representation,
	        OperationEnum operationEnum) {
		
		Operation operation = new Operation();
		operation.setName(operationName);
		operation.setDescritpion("finds all " + resourceName);
		List<String> produces = new ArrayList<String>();
		produces.add("application/json");
		operation.setProduces(produces);
		List<Parameter> parameters = getParametersList(representation.getProperties(), resourceName, operationEnum);
		operation.setParameters(parameters);
		
		Response statusOKResponse = new Response();
		statusOKResponse.setDescription(resourceName + " response");
		Schema schema = new Schema();
		if (operationEnum == OperationEnum.get || operationEnum == OperationEnum.getWithUUID)
			schema.setRef("#/definitions/" + resourceName);
		else if (operationEnum == OperationEnum.postCreate)
			schema.setRef("#/definitions/" + resourceName + "createInput");
		else if (operationEnum == OperationEnum.postUpdate)
			schema.setRef("#/definitions/" + resourceName + "updateInput");
		
		statusOKResponse.setSchema(schema);
		
		List<String> resourceTags = new ArrayList<String>();
		resourceTags.add(resourceName);
		operation.setTags(resourceTags);
		
		Map<String, Response> responses = new HashMap<String, Response>();
		responses.put("200", statusOKResponse);
		
		operation.setResponses(responses);
		
		return operation;
	}
	
	private static List<SearchHandlerDoc> fillSearchHandlers(List<SearchHandler> searchHandlers, String url) {
		
		List<SearchHandlerDoc> searchHandlerDocList = new ArrayList<SearchHandlerDoc>();
		String baseUrl = url.replace("/rest", "");
		for (SearchHandler searchHandler : searchHandlers) {
			
			SearchHandlerDoc searchHandlerDoc = new SearchHandlerDoc(searchHandler, baseUrl);
			searchHandlerDocList.add(searchHandlerDoc);
		}
		
		return searchHandlerDocList;
	}
	
	private String getResourceUrl(String baseUrl, String resourceName) {
		
		String resourceUrl = baseUrl;
		
		//Set the root url.
		return resourceUrl + "/" + resourceName;
		
	}
	
	private void serialize() {
		try {
			FileOutputStream fileOut = new FileOutputStream("C://Users//zakaria//Desktop//spec.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(swaggerSpecification);
			out.close();
			fileOut.close();
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
}
