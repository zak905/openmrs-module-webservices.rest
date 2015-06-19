package org.openmrs.module.webservices.docs.swagger;

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
import java.io.Serializable;

public class Parameter {
	
	private String name;
	
	private String in;
	
	private String description;
	
	private Boolean required;
	
	private String type = "string";
	
	public Parameter() {
		
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
	 * @return the in
	 */
	public String getIn() {
		return in;
	}
	
	/**
	 * @param in the in to set
	 */
	public void setIn(String in) {
		this.in = in;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the required
	 */
	public Boolean getRequired() {
		return required;
	}
	
	/**
	 * @param required the required to set
	 */
	public void setRequired(Boolean required) {
		this.required = required;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
}
