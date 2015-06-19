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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Operation {
	
	@JsonIgnore
	private String name;
	
	private String descritpion;
	
	private List<String> produces;
	
	private List<String> tags;
	
	private List<Parameter> parameters;
	
	private Map<String, Response> responses;
	
	public Operation() {
		
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the descritpion
	 */
	public String getDescritpion() {
		return descritpion;
	}
	
	/**
	 * @param descritpion the descritpion to set
	 */
	public void setDescritpion(String descritpion) {
		this.descritpion = descritpion;
	}
	
	/**
	 * @return the produces
	 */
	public List<String> getProduces() {
		return produces;
	}
	
	/**
	 * @param produces the produces to set
	 */
	public void setProduces(List<String> produces) {
		this.produces = produces;
	}
	
	/**
	 * @return the parameters
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}
	
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * @return the responses
	 */
	public Map<String, Response> getResponses() {
		return responses;
	}
	
	/**
	 * @param responses the responses to set
	 */
	public void setResponses(Map<String, Response> responses) {
		this.responses = responses;
	}
	
	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}
	
	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
}
