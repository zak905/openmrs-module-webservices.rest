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
package org.openmrs.module.webservices.rest.web.controller;

import java.util.List;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.docs.ResourceDoc;
import org.openmrs.module.webservices.docs.ResourceDocCreator;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

/**
 * Unit test for generating as much documentation as can be got from the source code.
 */
public class ResourceDocCreatorTest extends BaseModuleWebContextSensitiveTest {
	
	@Test
	public void createDocumentation() throws Exception {
		List<ResourceDoc> docs = ResourceDocCreator.create(Context.getAdministrationService().getGlobalProperty(
		    RestConstants.URI_PREFIX_GLOBAL_PROPERTY_NAME, "http://server:port/context/ws"));
		
		String header = "This page describes the urls/resources published by the [docs:Webservices.rest Module].";
		header += System.getProperty("line.separator");
		header += System.getProperty("line.separator");
		header += "For more information on conventions, authentication, and setup, see the main [docs:REST Web Services API] page.";
		header += System.getProperty("line.separator");
		
		System.out.println(header);
		
		for (ResourceDoc doc : docs) {
			
			String text = doc.toString();
			
			//Wiki syntax will think these are macros and complain like: Unknown macro: {......}
			text = text.replace("{", "\"");
			text = text.replace("}", "\"");
			
			System.out.println(text);
		}
	}
}
