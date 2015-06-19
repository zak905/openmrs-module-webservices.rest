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
import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

public class SwaggerSpecification {
	
	//Swagger Version
	private String swagger = "2.0";
	
	private Info info;
	
	private String host;
	
	private String basePath;
	
	private List<Tag> tags;
	
	private List<String> schemes;
	
	private List<String> consumes;
	
	private List<String> produces;
	
	private Paths paths;
	
	private Definitions definitions;
	
	public SwaggerSpecification() {
		
	}
	
	/**
	 * @return the info
	 */
	public Info getInfo() {
		return info;
	}
	
	/**
	 * @param info the info to set
	 */
	public void setInfo(Info info) {
		this.info = info;
	}
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return basePath;
	}
	
	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	/**
	 * @return the schemes
	 */
	public List<String> getSchemes() {
		return schemes;
	}
	
	/**
	 * @param schemes the schemes to set
	 */
	public void setSchemes(List<String> schemes) {
		this.schemes = schemes;
	}
	
	/**
	 * @return the consumes
	 */
	public List<String> getConsumes() {
		return consumes;
	}
	
	/**
	 * @param consumes the consumes to set
	 */
	public void setConsumes(List<String> consumes) {
		this.consumes = consumes;
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
	 * @return the paths
	 */
	public Paths getPaths() {
		return paths;
	}
	
	/**
	 * @param paths the paths to set
	 */
	public void setPaths(Paths paths) {
		this.paths = paths;
	}
	
	/**
	 * @return the definitions
	 */
	public Definitions getDefinitions() {
		return definitions;
	}
	
	/**
	 * @param definitions the definitions to set
	 */
	public void setDefinitions(Definitions definitions) {
		this.definitions = definitions;
	}
	
	/**
	 * @return the swagger
	 */
	public String getSwagger() {
		return swagger;
	}
	
	/**
	 * @return the tags
	 */
	public List<Tag> getTags() {
		return tags;
	}
	
	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
}
